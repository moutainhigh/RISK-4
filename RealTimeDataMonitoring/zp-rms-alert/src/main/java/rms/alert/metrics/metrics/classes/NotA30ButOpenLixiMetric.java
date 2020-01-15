package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.classes.SenderReceiverLixiOpen;
import rms.alert.data.classes.UserID;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.mynumberformat.MyNumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotA30ButOpenLixiMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(NotA30ButOpenLixiMetric.class);

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserID> getMetricData() {
        return dataManager.getUserIDNotA30(metricsConfig.getNotA30ButOpenLixiTimeTracking());
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
        if (!metricsConfig.getNotA30ButOpenLixiStatus().equals("on")) return;
        logger.info("A30 open lixi metric");
        Long threshold = Long.valueOf(metricsConfig.getNotA30ButOpenLixiThreshold());
        String timeTracking = metricsConfig.getNotA30ButOpenLixiTimeTracking();
        List<SenderReceiverLixiOpen> lixiOpenNotA30 = new ArrayList<>();

        List<UserID> userIDList = getMetricData();
        logger.info(userIDList.toString());

        List<SenderReceiverLixiOpen> lixiOpenList = dataManager.getSenderReceiverFromLixiOpen(timeTracking);
        logger.info(lixiOpenList.toString());
        for (SenderReceiverLixiOpen lixiOpen:lixiOpenList){
            for (UserID userZPI:userIDList){
                if (lixiOpen.getLixiReceiverZaloID()!=null && lixiOpen.getLixiReceiverZaloID().equals(userZPI.getUserID())){
                    lixiOpenNotA30.add(lixiOpen);
                }
            }
        }
        HashMap<String,ArrayList<String>> HMGroupID = new HashMap<String,ArrayList<String>>();
        HashMap<String,ArrayList<String>> HMGroupZID = new HashMap<String,ArrayList<String>>();
        HashMap<String,String> currentMapAmount = new HashMap<String,String>();
        for (SenderReceiverLixiOpen lixiOpen:lixiOpenNotA30){
            addToList(HMGroupID, lixiOpen.getReceiver(),lixiOpen.getGroupID(), false);
            addToList(HMGroupZID, lixiOpen.getReceiver(),lixiOpen.getGroupZID(), false);
            addAmountToTotalAmount(currentMapAmount,lixiOpen.getReceiver(),lixiOpen.getAmount());
        }
        logger.info(currentMapAmount.toString());
        if (currentMapAmount.keySet().size() >= threshold){
            String title = metricsConfig.getNotA30ButOpenLixiTitle();
            String resultSum = "";

            resultSum = formatForResult(currentMapAmount,HMGroupID,HMGroupZID,lixiOpenNotA30);

            String description = String.format(metricsConfig.getNotA30ButOpenLixiDescription(),
                    timeTracking, threshold, resultSum);
            logger.info("NotA30ButOpenLixi {} are added to alert content", resultSum);
            alert.addAlertContent(title, description, "default");
        }
        logger.info("A30 Open done");
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

    private String formatForResult(HashMap<String, String> map, HashMap<String, ArrayList<String>> HMGroupID,
                                   HashMap<String, ArrayList<String>> HMGroupZID, List<SenderReceiverLixiOpen> lixiOpenNotA30) {
        String result="";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String userID = entry.getKey();
            String amount = entry.getValue();
            int totalTransaction = getTotalTransaction(userID,lixiOpenNotA30);
            Long totalAmount = Long.parseLong(amount);
            String totalPackages = HMGroupID.get(userID).toString();
            String totalGroups = HMGroupZID.get(userID).toString();

            result+="<br><b>UserID:</b> "+userID+"<br>" +
                        "<b>Total transaction:</b> "+totalTransaction+"<br>" +
                        "<b>Total amount:</b> "+MyNumberFormat.CurrencyFormat(totalAmount)+"<br>"+
                        "<b>Total packages:</b> "+totalPackages+"<br>"+
                        "<b>Total groups:</b> "+totalGroups+"<br>";

        }
        return result;
    }
}