package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.CampaignAmount;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.concurrentproc.ConcurrentProc;
import rms.alert.utils.mynumberformat.MyNumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class CampaignAmountMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(CampaignAmountMetric.class);
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static List<List<CampaignAmount>> listTimeBefores = new ArrayList<>();
    private static List<CampaignAmount> listTimeTracking = new ArrayList<>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    @Override
    public void run() {
    	if (!metricsConfig.getCampaignAmountStatus().equals("on")) return;
    	
        Long threshold;
        String timeTracking = metricsConfig.getCampaignAmountTimeTracking();

        if (!getAllMetricData()) {
            listTimeBefores.clear();
            listTimeTracking.clear();
            logger.error("Get all metric data fail");
            return;
        };

        for (CampaignAmount ele : listTimeTracking) {
            threshold = calculateThreshold(ele.getCampaignID());
            int k = 6;
            if (threshold >= 1000000 && ele.getSumDiscount() >= k * threshold) {
                String title = metricsConfig.getCampaignAmountTitle();
                String description = String.format(metricsConfig.getCampaignAmountDescription(),
							                        timeTracking,
							                        MyNumberFormat.CurrencyFormat(threshold),
							                        ele.getCampaignID(),
							                        MyNumberFormat.CurrencyFormat(ele.getSumDiscount()),
							                        k);
                logger.info("CampaignID {} is added to alert content", ele.getCampaignID());
                alert.addAlertContent(title, description, "default");
            }
        }
        
        listTimeBefores.clear();
        listTimeTracking.clear();
    }
    
    private boolean getAllMetricData() {
    	String timeTracking = metricsConfig.getCampaignAmountTimeTracking();
        String timeBefore01 = metricsConfig.getCampaignAmountTimeBefore01();
        String timeBefore02 = metricsConfig.getCampaignAmountTimeBefore02();
        String timeBefore03 = metricsConfig.getCampaignAmountTimeBefore03();
        String timeBefore04 = metricsConfig.getCampaignAmountTimeBefore04();
        String timeBefore05 = metricsConfig.getCampaignAmountTimeBefore05();
        String timeBefore06 = metricsConfig.getCampaignAmountTimeBefore06();
        
        queue.add(() -> {
            List<CampaignAmount> listTimeBefore01 = dataManager.getCampaignAmount(timeTracking, timeBefore01);
            listTimeBefores.add(listTimeBefore01);
        });

        queue.add(() -> {
            List<CampaignAmount> listTimeBefore02 = dataManager.getCampaignAmount(timeTracking, timeBefore02);
            listTimeBefores.add(listTimeBefore02);
        });

        queue.add(() -> {
            List<CampaignAmount> listTimeBefore03 = dataManager.getCampaignAmount(timeTracking, timeBefore03);
            listTimeBefores.add(listTimeBefore03);
        });

        queue.add(() -> {
            List<CampaignAmount> listTimeBefore04 = dataManager.getCampaignAmount(timeTracking, timeBefore04);
            listTimeBefores.add(listTimeBefore04);
        });

        queue.add(() -> {
            List<CampaignAmount> listTimeBefore05 = dataManager.getCampaignAmount(timeTracking, timeBefore05);
            listTimeBefores.add(listTimeBefore05);
        });

        queue.add(() -> {
            List<CampaignAmount> listTimeBefore06 = dataManager.getCampaignAmount(timeTracking, timeBefore06);
            listTimeBefores.add(listTimeBefore06);
        });
        
        queue.add(() -> {
        	listTimeTracking = dataManager.getCampaignAmount(timeTracking);
        });

        ConcurrentProc threadPool = new ConcurrentProc();
        boolean res = threadPool.runningThreadPool(queue, queue.size());
        return res;
    }

    private Long calculateThreshold(String campaignID) {
        Long defaultThreshold = Long.parseLong(metricsConfig.getCampaignAmountThreshold());
        List<Long> sumDiscountList = new ArrayList<>();
        
        for (List<CampaignAmount> campaignAmountList : listTimeBefores) {
            Long sumDiscount = calculateSumDiscount(campaignID, campaignAmountList);
            if (sumDiscount != 0) sumDiscountList.add(sumDiscount);
        }
        
        Long temp = avgNumTrans(sumDiscountList);
        return (temp == 0L) ? defaultThreshold : temp;
    }

    private Long calculateSumDiscount(String campaignID, List<CampaignAmount> campaignAmountList) {
        Long res = 0L;
        for (CampaignAmount item : campaignAmountList) {
            if (item.getCampaignID().equals(campaignID)) {
                return item.getSumDiscount();
            }
        }
        return res;
    }

    private Long avgNumTrans(List<Long> sumDiscount) {
        Long res = Long.parseLong(metricsConfig.getCampaignAmountThreshold());
        if (sumDiscount.size() == 0) return res;
        res = 0L;
        for (Long ele : sumDiscount) res += ele;
        res = res / sumDiscount.size();
        return res;
    }

}
