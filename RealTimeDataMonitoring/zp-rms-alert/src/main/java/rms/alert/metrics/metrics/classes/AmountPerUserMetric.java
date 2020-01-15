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

import java.util.HashMap;
import java.util.Map;

@Component
public class AmountPerUserMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(AmountPerUserMetric.class);

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
        if (!metricsConfig.getAmountPerUserStatus().equals("on")) return;

        Long threshold = Long.parseLong(metricsConfig.getAmountPerUserThreshold());
        String timeTracking = metricsConfig.getAmountPerUserTimeTracking();

        Map hashMap = redisRepo.getAllAmountPerUser();
        hashMap.forEach((k, v) -> {
            logger.info("User: " + k + ", Amount: " + v);
        });

        if (!hashMap.isEmpty()){
            String result="";
            result = formatForResult((HashMap<String, String>) hashMap);
            if (result.length() > 0){
                String title = metricsConfig.getAmountPerUserTitle();
                String description = String.format(metricsConfig.getAmountPerUserDescription(),
                        timeTracking, MyNumberFormat.CurrencyFormat(threshold), result);
                logger.info("Amount per user {} are added to alert content", result);
                alert.addAlertContent(title, description, "default");
            }
        }
    }

    private String formatForResult(HashMap<String, String> map) {
        String result="";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] value = entry.getValue().split(":");
            Long amount = Long.valueOf(value[0]);
            Long threshold = Long.valueOf(metricsConfig.getAmountPerUserThreshold());
            if (amount > threshold){
                result+="<br><b>UserID:</b> "+entry.getKey()+"<br>" +
                        "<b>Total amount:</b> "+ MyNumberFormat.CurrencyFormat(Long.valueOf(value[0]))+"<br>"+
                        "<b>Packages claimed:</b> "+value[1]+"<br>"+
                        "<b>GroupZID:</b> "+value[2]+"<br>";
            }
        }
        return result;
    }
}