package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.classes.ShareLixi;
import rms.alert.data.repository.RedisRepo;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.chart.LineChart;
import rms.alert.utils.chart.TimeSeriesChart;
import rms.alert.utils.mynumberformat.MyNumberFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class RejectRateLixiMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(RejectRateLixiMetric.class);
    private HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    @Autowired
    private RedisRepo redisRepo;

    @Autowired
    private TimeSeriesChart timeSeriesChart;

    @Autowired
    private LineChart lineChart;

    public List<ShareLixi> getMetricData() {
        return dataManager.getShareLixiData(metricsConfig.getRejectRateLixiTimeTracking());
    }

    private void addToList(String mapKey, String value) {
        ArrayList<String> itemList = hashMap.get(mapKey);
        if (itemList == null) {
            itemList = new ArrayList<String>();
            itemList.add(value);
            hashMap.put(mapKey, itemList);
        } else {
            if (!itemList.contains(value))
                itemList.add(value);
        }
    }

    @Override
    public void run() {
        if (!metricsConfig.getRejectRateLixiStatus().equals("on")) return;

        String timeTracking = metricsConfig.getRejectRateLixiTimeTracking();
        List<ShareLixi> data = getMetricData();
        if (data.size() == 0) return;
        List<ShareLixi> rejectedUsers = new ArrayList<>();

        getRejectedUsers(data, rejectedUsers);

        int totalNumLixiTurn = data.size();
        int totalNumRejectTurn = rejectedUsers.size();
        Float rejectRateTurn = (float)totalNumRejectTurn/totalNumLixiTurn;
        String strRejectRateTurn = MyNumberFormat.to4Fixed(String.valueOf(rejectRateTurn));

        int totalUser = 0;
        totalUser = getTotalNumDistinctUsers(data,totalUser);
        if (totalUser == 0) return;
        int totalRejectUser = 0;
        totalRejectUser = getTotalNumDistinctUsers(rejectedUsers, totalRejectUser);
        Float rejectRateUser = (float)totalRejectUser/totalUser;
        String strRejectRateUser = MyNumberFormat.to4Fixed(String.valueOf(rejectRateUser));

        Long rejectNumberThreshold = Long.valueOf(metricsConfig.getRejectRateLixiRejectNumber());
        Float rejectRateThreshold = Float.valueOf(metricsConfig.getRejectRateLixiRejectRate());

        String allRejectRateTurn = updateRejectRateTurnToRedis(strRejectRateTurn);
        String allRejectRateUser = updateRejectRateUserToRedis(strRejectRateUser);

        drawChart(allRejectRateTurn,allRejectRateUser);

        if (totalRejectUser > rejectNumberThreshold && rejectRateTurn > rejectRateThreshold){
            String title = metricsConfig.getRejectRateLixiTitle();
            String description = String.format(metricsConfig.getRejectRateLixiDescription(),
                    timeTracking, " Reject number: "+rejectNumberThreshold+" users - Reject rate: "+rejectRateThreshold,
                    totalNumRejectTurn+"/"+totalNumLixiTurn,
                    strRejectRateTurn,
                    totalRejectUser+"/"+totalUser,
                    strRejectRateUser);

            logger.info("Reject number: {} - reject rate: {} is added to content alert", totalRejectUser, strRejectRateTurn);
            alert.addAlertContent(title, description, "RejectRate");
        }
    }

    private String updateRejectRateTurnToRedis(String rejectRate) {
        String oldRejectRate = redisRepo.getRejectRateTurn();
        String datetime = LocalDateTime.now().getDayOfMonth()+"/"+LocalDateTime.now().getMonthValue()+" "+LocalDateTime.now().getHour()+"h"+LocalDateTime.now().getMinute();
        rejectRate = datetime+"-"+rejectRate;

        String newRejectRate;
        if (oldRejectRate != null){
            newRejectRate = oldRejectRate+":"+rejectRate;
        }else
            newRejectRate = rejectRate;
        redisRepo.writeRejectRateTurn(newRejectRate);
        return newRejectRate;
    }

    private String updateRejectRateUserToRedis(String rejectRate) {
        String oldRejectRate = redisRepo.getRejectRateUser();
        String datetime = LocalDateTime.now().getDayOfMonth()+"/"+LocalDateTime.now().getMonthValue()+" "+LocalDateTime.now().getHour()+"h"+LocalDateTime.now().getMinute();
        rejectRate = datetime+"-"+rejectRate;

        String newRejectRate;
        if (oldRejectRate != null){
            newRejectRate = oldRejectRate+":"+rejectRate;
        }else
            newRejectRate = rejectRate;
        redisRepo.writeRejectRateUser(newRejectRate);
        return newRejectRate;
    }

    private void drawChart(String allRejectRateTurn, String allRejectRateUser) {
        timeSeriesChart.generateChart(allRejectRateTurn,allRejectRateUser);
        logger.info("Saved chart");
    }

    private int getTotalNumDistinctUsers(List<ShareLixi> rejectedUsers, int totalNumDistinctUsers) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        for (ShareLixi item : rejectedUsers){
            addToList(hashMap,"DistinctUser",item.getUserID(),false);
        }
        if (hashMap.containsKey("DistinctUser")){
            totalNumDistinctUsers = hashMap.get("DistinctUser").size();
        }
        return totalNumDistinctUsers;
    }

    private void getRejectedUsers(List<ShareLixi> data, List<ShareLixi> rejectedUsers) {
        for (ShareLixi item: data){
            String errorCode = item.getShareLixiErrorCode();
            String luckyMsg = item.getShareLixiLuckyMsg();
            Long amount = item.getAmount();
            if (errorCode!=null && !errorCode.equals("0")
                    || (luckyMsg!=null && luckyMsg.equals("true") && amount<1)){
                rejectedUsers.add(item);
            }
        }
    }

    private void addToList(HashMap<String, ArrayList<String>> HM, String mapKey, String value, boolean allowDuplication){
        ArrayList<String> itemList = HM.get(mapKey);
        if (itemList == null){
            itemList = new ArrayList<String>();
            itemList.add(value);
            HM.put(mapKey, itemList);
        } else{
            if (allowDuplication) itemList.add(value);
            else {
                if (!itemList.contains(value)) itemList.add(value);
            }
        }
    }
}
