package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.UserID;
import rms.alert.data.classes.UserLocation;
import rms.alert.data.repository.RedisRepo;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserOnSameLocationMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(UserOnSameLocationMetric.class);

    @Autowired
    private DataManager dataManager;

    @Autowired
    private RedisRepo redisRepo;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<UserLocation> getMetricData() {
        return dataManager.getNumUserOnEachLocation(metricsConfig.getUserLocationTimeTracking());
    }

    @Override
    public void run() {
    	if (!metricsConfig.getUserLocationStatus().equals("on")) return;
        Long threshold = Long.parseLong(metricsConfig.getUserLocationThreshold());
        String timeTracking = metricsConfig.getUserLocationTimeTracking();

        Map hashMap = redisRepo.getAllLocationWithNumUserCurrent();
        hashMap.forEach((k,v)->{
            logger.info("LatLng: "+k+", NumUser: "+v);
        });


        hashMap.forEach((key,value)-> {
            int numUser = Integer.parseInt(String.valueOf(value));
            if (numUser > threshold){
                String[] latLng = key.toString().split("-");
                List<UserID> userIDS = dataManager.getUserIDsForUserLocation(timeTracking, latLng[0], latLng[1]);

                HashMap<String, ArrayList<String>> retrieveHM = new HashMap<String, ArrayList<String>>();
                for (UserID item: userIDS){
                    addToList(retrieveHM,item.getCampaignID(),item.getUserID(),false);
                }

                String userIDList = "";
                for(Map.Entry<String, ArrayList<String>> item : retrieveHM.entrySet()) {
                    String campaignID = item.getKey();
                    userIDList+="<br>&emsp;&emsp;+ CampaignID: "+campaignID+" - UserID List: ";
                    String users = item.getValue().toString();
                    if (item.getValue().size() > 0){
                        users =  users.substring(1, users.length() - 1);
                    }
                    userIDList += users;
                }
                if (userIDS.size() >= Integer.parseInt(metricsConfig.getUserLocationNumUser())){
                    String title = metricsConfig.getUserLocationTitle();
                    String description = String.format(metricsConfig.getUserLocationDescription(),
                            timeTracking, threshold,
                            key,
                            userIDS.size(),
                            userIDList);

                    logger.info("{} users ({}) in the same lat-lng {}", userIDS.size(), userIDList, key);
                    alert.addAlertContent(title, description, "default");
                }
            }
        });

        hashMap.clear();
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
