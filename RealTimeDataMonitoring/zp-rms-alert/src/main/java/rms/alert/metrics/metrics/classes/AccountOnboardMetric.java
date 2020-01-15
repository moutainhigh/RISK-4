package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rms.alert.data.DataManager;
import rms.alert.data.classes.AccountOnboard;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.concurrentproc.ConcurrentProc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class AccountOnboardMetric implements Runnable {

    private final Logger logger = LogManager.getLogger(AccountOnboardMetric.class);
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static List<List<AccountOnboard>> listTimeBefores = new ArrayList<>();
    private static List<AccountOnboard> listTimeTracking = new ArrayList<>();

    @Autowired
    private DataManager dataManager;

    @Autowired
    private Alert alert;

    @Autowired
    private MetricsConfig metricsConfig;

    @Override
    public void run() {
    	if (!metricsConfig.getAccountOnboardStatus().equals("on")) return;
    	
        String timeTracking = metricsConfig.getAccountOnboardTimeTracking();

        if (!getAllMetricData()) {
            listTimeBefores.clear();
            listTimeTracking.clear();
            logger.error("Get all metric data fail");
            return;
        };

        Long threshold = calculateThreshold();

        for (AccountOnboard ele : listTimeTracking) {
            int k;
            if (threshold >= 100) k = 2;
            else k = 6;

            if (threshold >= 20 && ele.getNumAccountOnboard() >= k * threshold) {
                String title = metricsConfig.getAccountOnboardTitle();
                String description = String.format(metricsConfig.getAccountOnboardDescription(),
							                        timeTracking,
							                        metricsConfig.getAccountOnboardTimeBefore01(),
							                        metricsConfig.getAccountOnboardTimeBefore02(),
							                        metricsConfig.getAccountOnboardTimeBefore03(),
							                        metricsConfig.getAccountOnboardTimeBefore04(),
							                        threshold,
							                        ele.getNumAccountOnboard());
                logger.info("Total account onboard {} is added to alert content", ele.getNumAccountOnboard());
                alert.addAlertContent(title, description, "default");
            }
        }

        listTimeBefores.clear();
        listTimeTracking.clear();
    }

    private boolean getAllMetricData() {
    	String timeTracking = metricsConfig.getAccountOnboardTimeTracking();
        String timeBefore01 = metricsConfig.getAccountOnboardTimeBefore01();
        String timeBefore02 = metricsConfig.getAccountOnboardTimeBefore02();
        String timeBefore03 = metricsConfig.getAccountOnboardTimeBefore03();
        String timeBefore04 = metricsConfig.getAccountOnboardTimeBefore04();
        
        queue.add(() -> {
            List<AccountOnboard> listTimeBefore01 = dataManager.getAccountOnboard(timeTracking, timeBefore01);
            listTimeBefores.add(listTimeBefore01);
        });

        queue.add(() -> {
            List<AccountOnboard> listTimeBefore02 = dataManager.getAccountOnboard(timeTracking, timeBefore02);
            listTimeBefores.add(listTimeBefore02);
        });

        queue.add(() -> {
            List<AccountOnboard> listTimeBefore03 = dataManager.getAccountOnboard(timeTracking, timeBefore03);
            listTimeBefores.add(listTimeBefore03);
        });

        queue.add(() -> {
            List<AccountOnboard> listTimeBefore04 = dataManager.getAccountOnboard(timeTracking, timeBefore04);
            listTimeBefores.add(listTimeBefore04);
        });
        
        queue.add(() -> {
        	listTimeTracking = dataManager.getAccountOnboard(timeTracking);
        });

        ConcurrentProc threadPool = new ConcurrentProc();
        boolean res = threadPool.runningThreadPool(queue, queue.size());
        return res;
    }
    
    private Long calculateThreshold() {
        Long res = Long.parseLong(metricsConfig.getAccountOnboardThreshold());
        List<Long> numAccountOnboardList = new ArrayList<>();

        for (List<AccountOnboard> accountOnboardList : listTimeBefores) {
            if (accountOnboardList.size() > 0) {
                numAccountOnboardList.add(accountOnboardList.get(0).getNumAccountOnboard());
            }
        }
        Long temp = avgAccountOnboard(numAccountOnboardList);
        return (temp == 0L) ? res : temp;
    }

    private Long avgAccountOnboard(List<Long> numAccountOnboardList) {
        Long res = Long.parseLong(metricsConfig.getAccountOnboardThreshold());
        if (numAccountOnboardList.size() == 0) return res;
        res = 0L;
        for (Long ele : numAccountOnboardList) res += ele;
        res = res / numAccountOnboardList.size();
        return res;
    }
}