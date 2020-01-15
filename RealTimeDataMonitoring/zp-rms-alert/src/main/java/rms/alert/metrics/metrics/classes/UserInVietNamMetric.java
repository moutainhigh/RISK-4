package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.UserInVietNam;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserInVietNamMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(UserInVietNamMetric.class);
    private HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserInVietNam> getMetricData() {
        return dataManager.getUserInVietNam(metricsConfig.getUserInVietNamTimeTracking());
    }

    private void addToList(String mapKey, String value) {
        ArrayList<String> itemList = hashMap.get(mapKey);
        if (itemList == null) {
            itemList = new ArrayList<String>();
            itemList.add(value);
            hashMap.put(mapKey, itemList);
        } else {
            if (!itemList.contains(value)) itemList.add(value);
        }
    }

    @Override
    public void run() {
    	if (!metricsConfig.getUserInVietNamStatus().equals("on")) return;

        String threshold = metricsConfig.getUserInVietNamThreshold();
        String timeTracking = metricsConfig.getUserInVietNamTimeTracking();

        List<UserInVietNam> result = getMetricData();
        for (UserInVietNam item : result) {
            addToList(item.getCampaignID(),item.getIsInVietNam()+","+ item.getUserID());
        }

        logger.info("{}",hashMap.toString());

        int numUser = 0;
        String resultSum = "";
        for(Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            
            int tempCount = 0;
            for (String item:value){
                String[] arrRes = item.split(",");
                String isInVN = arrRes[0];
                String userID = arrRes[1];
                if (isInVN.equals(threshold))
                {
                	tempCount += 1;
                	if (tempCount == 1) {
                		resultSum+="<br>&emsp;&emsp;+ CampaignID: " + key + " - UserID List: ";
                	}
                	resultSum += userID + ", ";
                }
            }
            
            if (tempCount != 0) {
            	numUser += tempCount;
            	resultSum = resultSum.substring(0, resultSum.length() - 2);
            }
        }

        if (numUser >= Integer.parseInt(metricsConfig.getUserInVietNamNumUser())) {
            String title = metricsConfig.getUserInVietNamTitle();
            String description = String.format(metricsConfig.getUserInVietNamDescription(),
                    timeTracking,
                    numUser,
                    resultSum);
            logger.info("UserID {} (Not in Viet Nam) is added to alert content", resultSum);
            alert.addAlertContent(title, description, "default");
        }

        hashMap.clear();
    }
}
