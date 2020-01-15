package rms.alert.metrics.metrics.classes;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.repository.RedisRepo;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.mynumberformat.MyNumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NumGroupsPerUserMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(NumGroupsPerUserMetric.class);

    @Autowired
    private DataManager dataManager;

    @Autowired
    private RedisRepo redisRepo;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    @Autowired
    private RedisTemplate template;

    @Override
    public void run() {
        if (!metricsConfig.getNumGroupsPerUserStatus().equals("on")) return;

        Long threshold = Long.parseLong(metricsConfig.getNumGroupsPerUserThreshold());
        String timeTracking = metricsConfig.getNumGroupsPerUserTimeTracking();

        Map hashMap = redisRepo.getAllNumGroupsPerUser();

        hashMap.forEach((k, v) -> {
            logger.info("User: " + k + ", Groups: " + v);
        });

        if (!hashMap.isEmpty()){
            String result="";
            result = formatForResult((HashMap<String, String>) hashMap);
            if (result.length() > 0){
                String title = metricsConfig.getNumGroupsPerUserTitle();
                String description = String.format(metricsConfig.getNumGroupsPerUserDescription(),
                        timeTracking, threshold, result);
                logger.info("NumGroupsPerUser {} are added to alert content", result);
                alert.addAlertContent(title, description, "default");
            }
        }
    }

    private String formatForResult(HashMap<String, String> map) {
        String result="";
        Long threshold = Long.parseLong(metricsConfig.getNumGroupsPerUserThreshold());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] value = entry.getValue().split(":");

            List<String> listGroups = new ArrayList<>();
            String[] groups = value[1].split(",");
            for (String item:groups){
                listGroups.add(item);
            }
            if (listGroups.size() >= threshold){
                result+="<br><b>UserID:</b> "+entry.getKey()+"<br>" +
                    "<b>Total groups:</b> "+value[1]+"<br>"+
                    "<b>Total groups that user is one of the first members:</b> "+value[0]+"<br>";
            }
        }
        return result;
    }
}