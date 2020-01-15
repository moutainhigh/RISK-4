package rms.alert.metrics.metrics;

import rms.alert.metrics.metrics.classes.*;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.metrics.metrics.model.MetricModel;
import rms.alert.utils.interval.SetIntervalThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MetricsManager implements CommandLineRunner {

    private SetIntervalThreadPool siTP = new SetIntervalThreadPool();

    @Autowired
    private MetricsConfig metricsConfig;

    @Autowired
    private CampaignAmountMetric campaignAmountMetric;

    @Autowired
    private UserOnSameLocationMetric userOnSameLocationMetric;

    @Autowired
    private UserInVietNamMetric userInVietNamMetric;

    @Autowired
    private UserMNOMetric userMNOMetric;

    @Autowired
    private DeviceFactorsMetric deviceFactorsMetric;

    @Autowired
    private TransOfAllCampaignsMetric transOfAllCampaignsMetric;

    @Autowired
    private UserIPMetric userIPMetric;

    @Autowired
    private CardMappingMetric cardMappingMetric;

    @Autowired
    private AccountOnboardMetric accountOnboardMetric;

    @Autowired
    private Same2ndReceiverMetric same2ndReceiverMetric;

    @Autowired
    private AmountPerUserMetric amountPerUserMetric;

    @Autowired
    private RejectRateLixiMetric rejectRateLixiMetric;

    @Autowired
    private NumGroupsPerUserMetric numGroupsPerUserMetric;

    @Autowired
    private NotA30ButOpenLixiMetric notA30ButOpenLixiMetric;

    @Autowired
    private NotA30ButSpinMetric notA30ButSpinMetric;

    @Autowired
    private MetricModel metricModel;

    @Override
    public void run(String... args) throws Exception {
    	/* -----------------------------
    	 * Starting all normal metrics
    	 * -----------------------------
    	 */
//        while (true) {
//            if (LocalDateTime.now().getMinute() == 0 || LocalDateTime.now().getMinute() == 30) {
//                break;
//            }
//        }

//        siTP.startInterval(metricsConfig.getCampaignAmountWaitTime(), campaignAmountMetric);
//        siTP.startInterval(metricsConfig.getUserLocationWaitTime(), userOnSameLocationMetric);
//        siTP.startInterval(metricsConfig.getUserInVietNamWaitTime(), userInVietNamMetric);
//        siTP.startInterval(metricsConfig.getUserMNOWaitTime(), userMNOMetric);
//        siTP.startInterval(metricsConfig.getDeviceFactorsWaitTime(), deviceFactorsMetric);
//        siTP.startInterval(metricsConfig.getTransOfAllCampaignsWaitTime(), transOfAllCampaignsMetric);
//        siTP.startInterval(metricsConfig.getUserIPWaitTime(), userIPMetric);
//        siTP.startInterval(metricsConfig.getCardMappingWaitTime(), cardMappingMetric);
//        siTP.startInterval(metricsConfig.getAccountOnboardWaitTime(), accountOnboardMetric);
//        siTP.startInterval(metricsConfig.getSame2ndReceiverWaitTime(), same2ndReceiverMetric);
//        siTP.startInterval(metricsConfig.getAmountPerUserWaitTime(), amountPerUserMetric);
        siTP.startInterval(metricsConfig.getRejectRateLixiWaitTime(), rejectRateLixiMetric);
//        siTP.startInterval(metricsConfig.getNumGroupsPerUserWaitTime(), numGroupsPerUserMetric);
//
//        siTP.startInterval(metricsConfig.getNotA30ButOpenLixiWaitTime(), notA30ButOpenLixiMetric);
//        siTP.startInterval(metricsConfig.getNotA30ButSpinWaitTime(), notA30ButSpinMetric);
        /* ----------------------
    	 * Starting metric model
    	 * ----------------------
    	 */
//		while (true) {
//			if (LocalDateTime.now().getMinute() == 5 || LocalDateTime.now().getMinute() == 35) {
//				break;
//			}
//		}
        
      	siTP.startInterval(metricsConfig.getMetricModelWaitTime(), metricModel);
    }
}
