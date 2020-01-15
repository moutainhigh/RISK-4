package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.TransOfAllCampaigns;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.concurrentproc.ConcurrentProc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TransOfAllCampaignsMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(TransOfAllCampaignsMetric.class);
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static List<List<TransOfAllCampaigns>> listTimeBefores = new ArrayList<>();
    private static List<TransOfAllCampaigns> listTimeTracking = new ArrayList<>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    @Override
    public void run() {
    	if (!metricsConfig.getTransOfAllCampaignsStatus().equals("on")) return;
    	
        String timeTracking = metricsConfig.getTransOfAllCampaignsTimeTracking();
        
        if (!getAllMetricData()) {
            listTimeBefores.clear();
            listTimeTracking.clear();
            logger.error("Get all metric data fail");
            return;
        };

        Long countAllTrans = countAllTransOfAllCampaign(listTimeTracking);

        sortNumTransDescending(listTimeTracking);

        Long threshold = calculateThreshold();

        int k;
        if (threshold >= 100) k = 2;
        else k = 6;

        if (threshold >= 20 && countAllTrans >= k * threshold) {
            String title = metricsConfig.getTransOfAllCampaignsTitle();
            String top3Campaign = getTop3Campaign(listTimeTracking,countAllTrans);
            String description = String.format(metricsConfig.getTransOfAllCampaignsDescription(),
                    timeTracking,
                    metricsConfig.getTransOfAllCampaignsTimeBefore01(),
                    metricsConfig.getTransOfAllCampaignsTimeBefore02(),
                    metricsConfig.getTransOfAllCampaignsTimeBefore03(),
                    metricsConfig.getTransOfAllCampaignsTimeBefore04(),
                    threshold,
                    countAllTrans,top3Campaign, k);
            logger.info("Total transaction {} and top3 {} is added to alert content", countAllTrans,top3Campaign);
            alert.addAlertContent(title, description, "default");
        }
        
        listTimeBefores.clear();
        listTimeTracking.clear();
    }

    private boolean getAllMetricData() {
    	String timeTracking = metricsConfig.getTransOfAllCampaignsTimeTracking();
        String timeBefore01 = metricsConfig.getTransOfAllCampaignsTimeBefore01();
        String timeBefore02 = metricsConfig.getTransOfAllCampaignsTimeBefore02();
        String timeBefore03 = metricsConfig.getTransOfAllCampaignsTimeBefore03();
        String timeBefore04 = metricsConfig.getTransOfAllCampaignsTimeBefore04();
        
        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore01 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore01);
            listTimeBefores.add(listTimeBefore01);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore02 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore02);
            listTimeBefores.add(listTimeBefore02);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore03 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore03);
            listTimeBefores.add(listTimeBefore03);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore04 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore04);
            listTimeBefores.add(listTimeBefore04);
        });
        
        queue.add(() -> {
        	listTimeTracking = dataManager.getTransOfAllCampaigns(timeTracking);
        });

        ConcurrentProc threadPool = new ConcurrentProc();
        boolean res = threadPool.runningThreadPool(queue, queue.size());
        return res;
    }
    
    private Long calculateThreshold() {
        Long res = Long.parseLong(metricsConfig.getTransOfAllCampaignsThreshold());
        List<Long> numTrans = new ArrayList<>();

        for (List<TransOfAllCampaigns> transOfAllCampaignsList : listTimeBefores) {
            if (transOfAllCampaignsList.size() > 0) {
                numTrans.add(transOfAllCampaignsList.get(0).getNumTrans());
            }
        }
        
        Long temp = avgNumTrans(numTrans);
        return (temp == 0L) ? res : temp;
    }

    private Long avgNumTrans(List<Long> numTrans) {
        Long res = Long.parseLong(metricsConfig.getTransOfAllCampaignsThreshold());
        if (numTrans.size() == 0) return res;
        res = 0L;
        for (Long ele : numTrans) res += ele;
        res = res / numTrans.size();
        return res;
    }


    //Process Trans
    private Long countAllTransOfAllCampaign(List<TransOfAllCampaigns> listTimeTracking){
        Long res = 0L;
        for (TransOfAllCampaigns item:listTimeTracking)
        {
            res += item.getNumTrans();
        }
        return res;
    }

    private void sortNumTransDescending(List<TransOfAllCampaigns> listTimeTracking){
        if (listTimeTracking.size()>0){
            Comparator<TransOfAllCampaigns> compareByNumTrans = Comparator.comparing(TransOfAllCampaigns::getNumTrans);
            listTimeTracking.sort(compareByNumTrans.reversed());
        }
    }

    private String getTop3Campaign(List<TransOfAllCampaigns> listTimeTracking, Long countAllTrans) {
        String res="";
        if (listTimeTracking.size()>=1){
            TransOfAllCampaigns item = listTimeTracking.get(0);
            int percent = (int) (item.getNumTrans()*100/countAllTrans);
            res = "<br>campaignID: "+ item.getCampaignID()
                    +" - number of transactions: "+item.getNumTrans()
                    +"/"+countAllTrans+" ("+percent+"%)";
        }

        if (listTimeTracking.size()>=2){
            TransOfAllCampaigns item = listTimeTracking.get(1);
            int percent = (int) (item.getNumTrans()*100/countAllTrans);
            res += "<br>campaignID: "+ item.getCampaignID()
                    +" - number of transactions: "+item.getNumTrans()
                    +"/"+countAllTrans+" ("+percent+"%)";
        }

        if (listTimeTracking.size()>=3){
            TransOfAllCampaigns item = listTimeTracking.get(2);
            int percent = (int) (item.getNumTrans()*100/countAllTrans);
            res += "<br>campaignID: "+ item.getCampaignID()
                    +" - number of transactions: "+item.getNumTrans()
                    +"/"+countAllTrans+" ("+percent+"%)";
        }

        return res;
    }
}
