package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.CardMapping;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.concurrentproc.ConcurrentProc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CardMappingMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(CardMappingMetric.class);
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static List<List<CardMapping>> listTimeBefores = new ArrayList<>();
    private static List<CardMapping> listTimeTracking = new ArrayList<>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    public List<CardMapping> getMetricData() {
        return dataManager.getCardMapping(metricsConfig.getCardMappingTimeTracking());
    }

    @Override
    public void run() {
    	if (!metricsConfig.getCardMappingStatus().equals("on")) return;
    	
        String timeTracking = metricsConfig.getCardMappingTimeTracking();

        if (!getAllMetricData()) {
            listTimeBefores.clear();
            listTimeTracking.clear();
            logger.error("Get all metric data fail");
            return;
        };

        Long threshold = calculateThreshold();

        for (CardMapping ele : listTimeTracking) {
            int k;
            if (threshold >= 100) k = 2;
            else k = 6;

            if (threshold >= 20 && ele.getNumCardMapping() >= k * threshold) {
                String title = metricsConfig.getCardMappingTitle();
                String description = String.format(metricsConfig.getCardMappingDescription(),
							                        timeTracking,
							                        metricsConfig.getCardMappingTimeBefore01(),
							                        metricsConfig.getCardMappingTimeBefore02(),
							                        metricsConfig.getCardMappingTimeBefore03(),
							                        metricsConfig.getCardMappingTimeBefore04(),
							                        threshold,
							                        ele.getNumCardMapping());
                logger.info("Total card mapping {} is added to alert content", ele.getNumCardMapping());
                alert.addAlertContent(title, description, "default");
            }
        }
        
        listTimeBefores.clear();
        listTimeTracking.clear();
    }

    private boolean getAllMetricData() {
    	 String timeTracking = metricsConfig.getCardMappingTimeTracking();
         String timeBefore01 = metricsConfig.getCardMappingTimeBefore01();
         String timeBefore02 = metricsConfig.getCardMappingTimeBefore02();
         String timeBefore03 = metricsConfig.getCardMappingTimeBefore03();
         String timeBefore04 = metricsConfig.getCardMappingTimeBefore04();
         
         queue.add(() -> {
             List<CardMapping> listTimeBefore01 = dataManager.getCardMapping(timeTracking, timeBefore01);
             listTimeBefores.add(listTimeBefore01);
         });

         queue.add(() -> {
             List<CardMapping> listTimeBefore02 = dataManager.getCardMapping(timeTracking, timeBefore02);
             listTimeBefores.add(listTimeBefore02);
         });

         queue.add(() -> {
             List<CardMapping> listTimeBefore03 = dataManager.getCardMapping(timeTracking, timeBefore03);
             listTimeBefores.add(listTimeBefore03);
         });

         queue.add(() -> {
             List<CardMapping> listTimeBefore04 = dataManager.getCardMapping(timeTracking, timeBefore04);
             listTimeBefores.add(listTimeBefore04);
         });

         queue.add(() -> {
        	 listTimeTracking = dataManager.getCardMapping(timeTracking);
         });
         
         ConcurrentProc threadPool = new ConcurrentProc();
         boolean res = threadPool.runningThreadPool(queue, queue.size());
         return res;
    }
    
    private Long calculateThreshold() {
        Long res = Long.parseLong(metricsConfig.getCardMappingThreshold());
        List<Long> numTrans = new ArrayList<>();

        for (List<CardMapping> cardMappingList : listTimeBefores) {
            if (cardMappingList.size() > 0) {
                numTrans.add(cardMappingList.get(0).getNumCardMapping());
            }
        }
        
        Long temp = avgCardMapping(numTrans);
        return (temp == 0L) ? res : temp;
    }

    private Long avgCardMapping(List<Long> numCardMapping) {
        Long res = Long.parseLong(metricsConfig.getCardMappingThreshold());
        if (numCardMapping.size() == 0) return res;
        res = 0L;
        for (Long ele : numCardMapping) res += ele;
        res = res / numCardMapping.size();
        return res;
    }
}
