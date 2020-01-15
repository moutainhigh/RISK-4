package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.classes.SenderReceiverLixiOpen;
import rms.alert.data.classes.ShareLixi;
import rms.alert.data.classes.UserID;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.lang.reflect.Array;
import java.util.*;

@Component
public class NotA30ButSpinMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(NotA30ButSpinMetric.class);

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserID> getMetricData() {
        return dataManager.getUserIDNotA30(metricsConfig.getNotA30ButSpinTimeTracking());
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

    @Override
    public void run() {
        if (!metricsConfig.getNotA30ButSpinStatus().equals("on")) return;
        logger.info("A30 spin metric");
        Long threshold = Long.valueOf(metricsConfig.getNotA30ButSpinThreshold());
        String timeTracking = metricsConfig.getNotA30ButSpinTimeTracking();
        List<ShareLixi> shareLixiNotA30 = new ArrayList<>();

        List<UserID> userIDList = getMetricData();
        logger.info(userIDList.toString());

        List<ShareLixi> shareLixiList = dataManager.getShareLixiData(timeTracking);
        logger.info(shareLixiList.toString());

        for (ShareLixi shareLixi:shareLixiList){
            for (UserID userZPI:userIDList){
                if (shareLixi.getShareLixiZaloID()!=null && shareLixi.getShareLixiZaloID().equals(userZPI.getUserID())){
                    shareLixiNotA30.add(shareLixi);
                }
            }
        }

        logger.info("ShareLixiNotA30: "+shareLixiNotA30.toString());

        List<ShareLixi> rejectedUsers = new ArrayList<>();
        List<ShareLixi> successUsers = new ArrayList<>();

        getSuccessUsers(shareLixiNotA30,successUsers);
        getRejectedUsers(shareLixiNotA30,rejectedUsers);

        logger.info("Success user: "+successUsers.toString());
        logger.info("Reject user: "+rejectedUsers.toString());

        HashMap<String,ArrayList<String>> currentMapSuccess = new HashMap<String,ArrayList<String>>();
        HashMap<String,ArrayList<String>> currentMapReject = new HashMap<String,ArrayList<String>>();
        for (ShareLixi shareLixi:successUsers){
            addToList(currentMapSuccess,shareLixi.getUserID(),"1",true);
        }

        for (ShareLixi shareLixi:rejectedUsers){
            addToList(currentMapReject,shareLixi.getUserID(),"1",true);
        }
        logger.info("Reject: "+currentMapReject.toString());
        logger.info("Success: "+currentMapSuccess.toString());

        HashMap<String, ArrayList<String>> total = new HashMap<String, ArrayList<String>>();
        total = getTotal(currentMapReject,currentMapSuccess);

        if (total.keySet().size() >= threshold){
            String title = metricsConfig.getNotA30ButSpinTitle();
            String resultSum = "";
            resultSum = formatForResult(total,currentMapReject,currentMapSuccess);
            String description = String.format(metricsConfig.getNotA30ButSpinDescription(),
                    timeTracking, threshold, resultSum);
            logger.info("NotA30ButSpin {} are added to alert content", resultSum);
            alert.addAlertContent(title, description, "default");
        }
        logger.info("A30 Spin done");
    }

    private void getSuccessUsers(List<ShareLixi> data, List<ShareLixi> successUsers) {
        for (ShareLixi item: data){
            String errorCode = item.getShareLixiErrorCode();
            String luckyMsg = item.getShareLixiLuckyMsg();
            Long amount = item.getAmount();
            if ((errorCode!=null && errorCode.equals("0"))
                    || (luckyMsg!=null && luckyMsg.equals("false") && amount>=1)){
                successUsers.add(item);
            }
        }
    }

    private int getTotalTransaction(String userID, List<SenderReceiverLixiOpen> lixiOpenNotA30) {
        int res = 0;
        for (SenderReceiverLixiOpen item : lixiOpenNotA30){
            if (item.getReceiver().equals(userID)||item.getSender().equals(userID)){
                res++;
            }
        }
        return res;
    }

    private void addAmountToTotalAmount(HashMap<String, String> currentMapAmount, String receiver, String amount) {
        String totalAmount = currentMapAmount.get(receiver);
        if (totalAmount == null){
            long res = (long) Double.parseDouble(amount);
            totalAmount = String.valueOf(res);
        }else{
            double numAmount = Double.parseDouble(totalAmount);
            numAmount+=Double.parseDouble(amount);
            long res = (long) numAmount;
            totalAmount = String.valueOf(res);
        }
        currentMapAmount.put(receiver,totalAmount);
    }

    private String formatForResult(HashMap<String, ArrayList<String>> total, HashMap<String, ArrayList<String>> rejectUsers,
                                   HashMap<String, ArrayList<String>> successUsers) {
        String result="";
        for (Map.Entry<String, ArrayList<String>> entry : total.entrySet()) {
            String userID = entry.getKey();
            int totalReject =  rejectUsers.containsKey(userID)?rejectUsers.get(userID).size():0;
            int totalSuccess = successUsers.containsKey(userID)? successUsers.get(userID).size():0;
            double rejectRate = (double) totalReject/(totalReject+totalSuccess);

            result+="<br><b>UserID:</b> "+userID+"<br>" +
                    "<b>Total reject:</b> "+totalReject+"<br>" +
                    "<b>Total success:</b> "+totalSuccess+"<br>"+
                    "<b>Reject rate:</b> "+rejectRate+"<br>";
        }
        return result;
    }

    private HashMap<String, ArrayList<String>> getTotal(HashMap<String, ArrayList<String>> rejectUsers, HashMap<String, ArrayList<String>> successUsers) {
        HashMap<String,ArrayList<String>> res = new HashMap<String,ArrayList<String>>();
        rejectUsers.forEach((k,v)->{
            addToList(res,k,"1",false);
        });
        successUsers.forEach((k,v)->{
            addToList(res,k,"1",false);
        });
        return res;
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
}