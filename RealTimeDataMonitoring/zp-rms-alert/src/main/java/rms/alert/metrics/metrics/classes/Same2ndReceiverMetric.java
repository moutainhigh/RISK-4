package rms.alert.metrics.metrics.classes;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.classes.UserLocation;
import rms.alert.data.repository.RedisRepo;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.mynumberformat.MyNumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Same2ndReceiverMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(Same2ndReceiverMetric.class);

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
        if (!metricsConfig.getSame2ndReceiverStatus().equals("on")) return;

        Long threshold = Long.parseLong(metricsConfig.getSame2ndReceiverThreshold());
        String timeTracking = metricsConfig.getSame2ndReceiverTimeTracking();

        Map hashMap = redisRepo.getAllListSenderOfReceiver();
        hashMap.forEach((k, v) -> {
            logger.info("Receiver: " + k + ", Senders: " + v);
        });

        if (!hashMap.isEmpty()){
            String result="";
            result = formatForResult((HashMap<String, String>) hashMap);
            if (result.length()>0){
                String title = metricsConfig.getSame2ndReceiverTitle();
                String description = String.format(metricsConfig.getSame2ndReceiverDescription(),
                        timeTracking, threshold, result);
                logger.info("Same 2nd receiver {} are added to alert content", result);
                alert.addAlertContent(title, description, "default");
            }
        }
    }

    private String formatForResult(HashMap<String, String> map) {
        String result="";
        Long threshold = Long.parseLong(metricsConfig.getSame2ndReceiverThreshold());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] value = entry.getValue().split(":");

            List<String> listSenders = new ArrayList<>();
            String[] senders = value[0].split(",");
            for (String item:senders){
                listSenders.add(item);
            }

            if (listSenders.size() >= threshold){
                result+="<br><b>Receiver:</b> "+entry.getKey()+"<br>" +
                        "<b>Senders:</b> "+value[0]+"<br>"+
                        "<b>Total amount:</b> "+ MyNumberFormat.CurrencyFormat(Long.valueOf(value[1]))+"<br>";
            }
        }
        return result;
    }
}