package rms.alert.metrics.metrics.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.UserIP;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

@Component
public class UserIPMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(UserIPMetric.class);
    private HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserIP> getMetricData() {
        return dataManager.getUserIP(metricsConfig.getUserIPTimeTracking());
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
    	if (!metricsConfig.getUserIPStatus().equals("on")) return;
    	
        String threshold = metricsConfig.getUserIPThreshold();
        String timeTracking = metricsConfig.getUserIPTimeTracking();
        List<UserIP> result = getMetricData();

        for (UserIP item : result) {
            String time = item.getTime().split("\\.")[0];
            addToList(hashMap, item.getCampaignID() + "," + item.getUserIP() + "," + time, item.getUserIP(), true);
        }

        HashMap<String, ArrayList<String>> retrieveHM = new HashMap<String, ArrayList<String>>();
        for(Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            if (value.size() > 1) {
            	String arrRes[] = key.split(",");
            	String campaignID = arrRes[0];
            	String userIP = arrRes[1];
            	
            	addToList(retrieveHM, campaignID, userIP, false);
            }
        }

        String resultSum = "";
        String resultLog = "";
        for(Map.Entry<String, ArrayList<String>> entry : retrieveHM.entrySet()) {
            String campaignID = entry.getKey();
            String userIPList = entry.getValue().toString();
            userIPList = userIPList.substring(1, userIPList.length() - 1);
            resultSum += "<br>&emsp;&emsp;+ CampaignID: " + campaignID + " - UserID List: " + userIPList;
            resultLog += "[" + campaignID + " - " + userIPList + "]";
        }
        
        if (resultSum.length() != 0){
            String title = metricsConfig.getUserIPTitle();
            String description = String.format(metricsConfig.getUserIPDescription(),
						                   timeTracking, threshold, resultSum, threshold);
            logger.info("CampaignID and UserIP {} are added to alert content", resultLog);
            alert.addAlertContent(title, description, "default");
        }
        
        hashMap.clear();
    }
}
