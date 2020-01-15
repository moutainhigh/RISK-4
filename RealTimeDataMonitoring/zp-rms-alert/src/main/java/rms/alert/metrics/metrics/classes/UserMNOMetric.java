package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.UserMNO;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.util.*;

@Component
public class UserMNOMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(UserMNOMetric.class);
    private HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserMNO> getMetricData() {
        return dataManager.getUserMNO(metricsConfig.getUserMNOTimeTracking());
    }

    private boolean isValidMNO(List<String> ValidMNOList, String mno) {
        mno = mno.trim().toLowerCase();
        
        for (String str : ValidMNOList) {
            str = str.trim().toLowerCase();
            if (str.equals("452")) {
                if (mno.length() < 3) continue;
                String mnoTmp = mno.substring(0, 3);
                if (mnoTmp.equals(str)) return true;
            } else if (mno.contains(str)) return true;
        }
        return false;
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
    	if (!metricsConfig.getUserMNOStatus().equals("on")) return;
    	
        List<String> ValidMNOList = Arrays.asList(metricsConfig.getUserMNOValidData().split(","));
        String timeTracking = metricsConfig.getUserMNOTimeTracking();

        List<UserMNO> result = getMetricData();

        for (UserMNO item : result) {
            addToList(hashMap,item.getCampaignID(), item.getMno()+","+item.getUserID(),false);
        }

        int numUser = 0;
        String resultSum = "";
        for(Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            
            int tempCount = 0;
            for (String item:value){
                String[] arrRes = item.split(",");
                String mno = arrRes[0];
                String userID = arrRes[1];
                if (!isValidMNO(ValidMNOList, mno))
                {
                	tempCount += 1;
                	if (tempCount == 1) {
                		resultSum+="<br>&emsp;&emsp;+ CampaignID: "+key;
                	}
                    resultSum += "<br>&emsp;&emsp;&emsp;&emsp;> " + mno + " - " + userID;
                }
            }
            
            numUser += tempCount;
        }
        
        if (numUser >= Integer.parseInt(metricsConfig.getUserMNONumUser())) {
            String title = metricsConfig.getUserMNOTitle();
            String description = String.format(metricsConfig.getUserMNODescription(), timeTracking, numUser, resultSum);
            logger.info("UserID {} with unsuitable MNO is added to alert content", resultSum);
            alert.addAlertContent(title, description, "default");
        }
        
        hashMap.clear();
    }

}
