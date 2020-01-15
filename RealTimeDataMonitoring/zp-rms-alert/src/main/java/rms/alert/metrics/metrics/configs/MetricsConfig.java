package rms.alert.metrics.metrics.configs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Configuration
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/metrics.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/metrics.properties")
public class MetricsConfig {

	private static final Logger logger = LogManager.getLogger();

	// MetricModel
	@Value("${MetricModel.status}")
	private String metricModelStatus;
	
	@Value("${MetricModel.wait.time}")
	private String metricModelWaitTime;
	
	@Value("${MetricModel.title}")
	private String metricModelTitle;
	
	private static String metricModelDescription = "";
	
	// CampaignAmount
	@Value("${CampaignAmount.status}")
	private String campaignAmountStatus;
	
	@Value("${CampaignAmount.wait.time}")
	private String campaignAmountWaitTime;

	@Value("${CampaignAmount.timeTracking}")
	private String campaignAmountTimeTracking;

	@Value("${CampaignAmount.threshold}")
	private String campaignAmountThreshold;

	@Value("${CampaignAmount.timeBefore01}")
	private String campaignAmountTimeBefore01;

	@Value("${CampaignAmount.timeBefore02}")
	private String campaignAmountTimeBefore02;

	@Value("${CampaignAmount.timeBefore03}")
	private String campaignAmountTimeBefore03;

	@Value("${CampaignAmount.timeBefore04}")
	private String campaignAmountTimeBefore04;

	@Value("${CampaignAmount.timeBefore05}")
	private String campaignAmountTimeBefore05;

	@Value("${CampaignAmount.timeBefore06}")
	private String campaignAmountTimeBefore06;

	@Value("${CampaignAmount.title}")
	private String campaignAmountTitle;

	private static String campaignAmountDescription = "";

	// DeviceFactors
	@Value("${DeviceFactors.status}")
	private String deviceFactorsStatus;
	
	@Value("${DeviceFactors.wait.time}")
	private String deviceFactorsWaitTime;

	@Value("${DeviceFactors.timeTracking}")
	private String deviceFactorsTimeTracking;

	@Value("${DeviceFactors.threshold}")
	private String deviceFactorsThreshold;

	@Value("${DeviceFactors.title}")
	private String deviceFactorsTitle;

	private static String deviceFactorsDescription = "";

	// TransPerAllCampaigns
	@Value("${TransOfAllCampaigns.status}")
	private String transOfAllCampaignsStatus;
	
	@Value("${TransOfAllCampaigns.wait.time}")
	private String transOfAllCampaignsWaitTime;

	@Value("${TransOfAllCampaigns.timeTracking}")
	private String transOfAllCampaignsTimeTracking;

	@Value("${TransOfAllCampaigns.timeBefore01}")
	private String transOfAllCampaignsTimeBefore01;

	@Value("${TransOfAllCampaigns.timeBefore02}")
	private String transOfAllCampaignsTimeBefore02;

	@Value("${TransOfAllCampaigns.timeBefore03}")
	private String transOfAllCampaignsTimeBefore03;

	@Value("${TransOfAllCampaigns.timeBefore04}")
	private String transOfAllCampaignsTimeBefore04;

	@Value("${TransOfAllCampaigns.threshold}")
	private String transOfAllCampaignsThreshold;

	@Value("${TransOfAllCampaigns.title}")
	private String transOfAllCampaignsTitle;

	private static String transOfAllCampaignsDescription = "";

	// UserInVietNam
	@Value("${UserInVietNam.status}")
	private String userInVietNamStatus;
	
	@Value("${UserInVietNam.wait.time}")
	private String userInVietNamWaitTime;

	@Value("${UserInVietNam.timeTracking}")
	private String userInVietNamTimeTracking;

	@Value("${UserInVietNam.threshold}")
	private String userInVietNamThreshold;

	@Value("${UserInVietNam.numUser}")
	private String userInVietNamNumUser;

	@Value("${UserInVietNam.title}")
	private String userInVietNamTitle;

	private static String userInVietNamDescription = "";

	// UserLocation
	@Value("${UserLocation.status}")
	private String userLocationStatus;
	
	@Value("${UserLocation.wait.time}")
	private String userLocationWaitTime;

	@Value("${UserLocation.timeTracking}")
	private String userLocationTimeTracking;

	@Value("${UserLocation.threshold}")
	private String userLocationThreshold;

	@Value("${UserLocation.numUser}")
	private String userLocationNumUser;

	@Value("${UserLocation.title}")
	private String userLocationTitle;

	private static String userLocationDescription = "";

	// UserMNO
	@Value("${UserMNO.status}")
	private String userMNOStatus;
	
	@Value("${UserMNO.wait.time}")
	private String userMNOWaitTime;

	@Value("${UserMNO.timeTracking}")
	private String userMNOTimeTracking;

	@Value("${UserMNO.validData}")
	private String userMNOValidData;

	@Value("${UserMNO.numUser}")
	private String userMNONumUser;

	@Value("${UserMNO.title}")
	private String userMNOTitle;

	private static String userMNODescription = "";

	// UserIP
	@Value("${UserIP.status}")
	private String userIPStatus;
	
	@Value("${UserIP.wait.time}")
	private String userIPWaitTime;

	@Value("${UserIP.timeTracking}")
	private String userIPTimeTracking;

	@Value("${UserIP.threshold}")
	private String userIPThreshold;

	@Value("${UserIP.title}")
	private String userIPTitle;

	private static String userIPDescription = "";

	// Card mapping
	@Value("${CardMapping.status}")
	private String cardMappingStatus;
	
	@Value("${CardMapping.wait.time}")
	private String cardMappingWaitTime;

	@Value("${CardMapping.timeTracking}")
	private String cardMappingTimeTracking;

	@Value("${CardMapping.threshold}")
	private String cardMappingThreshold;

	@Value("${CardMapping.timeBefore01}")
	private String cardMappingTimeBefore01;

	@Value("${CardMapping.timeBefore02}")
	private String cardMappingTimeBefore02;

	@Value("${CardMapping.timeBefore03}")
	private String cardMappingTimeBefore03;

	@Value("${CardMapping.timeBefore04}")
	private String cardMappingTimeBefore04;

	@Value("${CardMapping.title}")
	private String cardMappingTitle;

	private static String cardMappingDescription = "";

	// Account onboarded
	@Value("${AccountOnboard.status}")
	private String accountOnboardStatus;
	
	@Value("${AccountOnboard.wait.time}")
	private String accountOnboardWaitTime;

	@Value("${AccountOnboard.timeTracking}")
	private String accountOnboardTimeTracking;

	@Value("${AccountOnboard.threshold}")
	private String accountOnboardThreshold;

	@Value("${AccountOnboard.timeBefore01}")
	private String accountOnboardTimeBefore01;

	@Value("${AccountOnboard.timeBefore02}")
	private String accountOnboardTimeBefore02;

	@Value("${AccountOnboard.timeBefore03}")
	private String accountOnboardTimeBefore03;

	@Value("${AccountOnboard.timeBefore04}")
	private String accountOnboardTimeBefore04;

	@Value("${AccountOnboard.title}")
	private String accountOnboardTitle;

	private static String accountOnboardDescription = "";

	//TET Campaign - Same 2nd Receiver
	@Value("${Same2ndReceiver.status}")
	private String same2ndReceiverStatus;

	@Value("${Same2ndReceiver.wait.time}")
	private String same2ndReceiverWaitTime;

	@Value("${Same2ndReceiver.timeTracking}")
	private String same2ndReceiverTimeTracking;

	@Value("${Same2ndReceiver.threshold}")
	private String same2ndReceiverThreshold;

	@Value("${Same2ndReceiver.title}")
	private String Same2ndReceiverTitle;

	private static String same2ndReceiverDescription = "";

	//TET Campaign - Amount per user
	@Value("${AmountPerUser.status}")
	private String AmountPerUserStatus;

	@Value("${AmountPerUser.wait.time}")
	private String AmountPerUserWaitTime;

	@Value("${AmountPerUser.timeTracking}")
	private String AmountPerUserTimeTracking;

	@Value("${AmountPerUser.threshold}")
	private String AmountPerUserThreshold;

	@Value("${AmountPerUser.title}")
	private String AmountPerUserTitle;

	private static String AmountPerUserDescription = "";

	//TET Campaign - Number of groups created by 1 user
	@Value("${NumGroupsPerUser.status}")
	private String numGroupsPerUserStatus;

	@Value("${NumGroupsPerUser.wait.time}")
	private String numGroupsPerUserWaitTime;

	@Value("${NumGroupsPerUser.timeTracking}")
	private String numGroupsPerUserTimeTracking;

	@Value("${NumGroupsPerUser.threshold}")
	private String numGroupsPerUserThreshold;

	@Value("${NumGroupsPerUser.title}")
	private String numGroupsPerUserTitle;

	private static String numGroupsPerUserDescription = "";


	//TET Campaign - Reject rate
	@Value("${RejectRateLixi.status}")
	private String rejectRateLixiStatus;

	@Value("${RejectRateLixi.wait.time}")
	private String rejectRateLixiWaitTime;

	@Value("${RejectRateLixi.timeTracking}")
	private String rejectRateLixiTimeTracking;

	@Value("${RejectRateLixi.rejectNumber}")
	private String rejectRateLixiRejectNumber;

	@Value("${RejectRateLixi.rejectRate}")
	private String rejectRateLixiRejectRate;

	@Value("${RejectRateLixi.title}")
	private String rejectRateLixiTitle;

	private static String rejectRateLixiDescription = "";

	//TET Campaign - Not meet A30 but open lixi
	@Value("${NotA30ButOpenLixi.status}")
	private String NotA30ButOpenLixiStatus;

	@Value("${NotA30ButOpenLixi.wait.time}")
	private String NotA30ButOpenLixiWaitTime;

	@Value("${NotA30ButOpenLixi.timeTracking}")
	private String NotA30ButOpenLixiTimeTracking;

	@Value("${NotA30ButOpenLixi.threshold}")
	private String NotA30ButOpenLixiThreshold;

	@Value("${NotA30ButOpenLixi.title}")
	private String NotA30ButOpenLixiTitle;

	private static String NotA30ButOpenLixiDescription = "";

	//TET Campaign - Not meet A30 but open spin
	@Value("${NotA30ButSpin.status}")
	private String NotA30ButSpinStatus;

	@Value("${NotA30ButSpin.wait.time}")
	private String NotA30ButSpinWaitTime;

	@Value("${NotA30ButSpin.timeTracking}")
	private String NotA30ButSpinTimeTracking;

	@Value("${NotA30ButSpin.threshold}")
	private String NotA30ButSpinThreshold;

	@Value("${NotA30ButSpin.title}")
	private String NotA30ButSpinTitle;

	private static String NotA30ButSpinDescription = "";

	//AutoUpdate
	@Value("${AutoUpdateMetricData.status}")
	private String autoUpdateMetricDataStatus;

	@Value("${AutoUpdateMetricData.wait.time}")
	private String autoUpdateMetricDataWaitTime;

	@Value("${AutoUpdateMetricData.timeTracking}")
	private String getAutoUpdateMetricDataTimeTracking;

	// Load html
	@Autowired
	private Environment environment;

	private String getConfigPath() {
		try {
			return environment.getProperty("app.path").trim();
		} catch (Exception e) {
			return ".";
		}
	}

	private static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			logger.error("Read HTML mail design file fail", e);
		}
		return contentBuilder.toString();
	}

	@PostConstruct
	private void loadHTMLDescription() {
		metricModelDescription = readLineByLineJava8(getConfigPath() + "/conf/html/metricModel.html");
		userMNODescription = readLineByLineJava8(getConfigPath() + "/conf/html/mno.html");
		userLocationDescription = readLineByLineJava8(getConfigPath() + "/conf/html/userLocation.html");
		userInVietNamDescription = readLineByLineJava8(getConfigPath() + "/conf/html/inVietNam.html");
		transOfAllCampaignsDescription = readLineByLineJava8(getConfigPath() + "/conf/html/transOfAllCampaigns.html");
		campaignAmountDescription = readLineByLineJava8(getConfigPath() + "/conf/html/campaignAmount.html");
		deviceFactorsDescription = readLineByLineJava8(getConfigPath() + "/conf/html/deviceFactors.html");
		userIPDescription = readLineByLineJava8(getConfigPath() + "/conf/html/userIP.html");
		cardMappingDescription = readLineByLineJava8(getConfigPath() + "/conf/html/cardMapping.html");
		accountOnboardDescription = readLineByLineJava8(getConfigPath() + "/conf/html/accountOnboard.html");
		same2ndReceiverDescription = readLineByLineJava8(getConfigPath() + "/conf/html/same2ndReceiver.html");
		AmountPerUserDescription = readLineByLineJava8(getConfigPath() + "/conf/html/amountPerUser.html");
		numGroupsPerUserDescription = readLineByLineJava8(getConfigPath() + "/conf/html/numGroupsPerUser.html");
		rejectRateLixiDescription = readLineByLineJava8(getConfigPath() + "/conf/html/rejectRateLixi.html");
		NotA30ButOpenLixiDescription = readLineByLineJava8(getConfigPath() + "/conf/html/notA30ButOpenLixi.html");
		NotA30ButSpinDescription = readLineByLineJava8(getConfigPath() + "/conf/html/notA30ButSpin.html");

	}

	// Functions
	public String getMetricModelWaitTime() {
		return metricModelWaitTime;
	}

	public String getMetricModelStatus() {
		return metricModelStatus;
	}
	
	public String getMetricModelTitle() {
		return metricModelTitle;
	}
	
	public String getMetricModelDescription() {
		return metricModelDescription;
	}
	
	public String getCampaignAmountWaitTime() {
		return campaignAmountWaitTime;
	}

	public String getCampaignAmountTimeTracking() {
		return campaignAmountTimeTracking;
	}

	public String getCampaignAmountThreshold() {
		return campaignAmountThreshold;
	}

	public String getCampaignAmountTitle() {
		return campaignAmountTitle;
	}

	public String getCampaignAmountDescription() {
		return campaignAmountDescription;
	}

	public String getDeviceFactorsWaitTime() {
		return deviceFactorsWaitTime;
	}

	public String getDeviceFactorsTimeTracking() {
		return deviceFactorsTimeTracking;
	}

	public String getDeviceFactorsThreshold() {
		return deviceFactorsThreshold;
	}

	public String getDeviceFactorsTitle() {
		return deviceFactorsTitle;
	}

	public String getDeviceFactorsDescription() {
		return deviceFactorsDescription;
	}

	public String getTransOfAllCampaignsWaitTime() {
		return transOfAllCampaignsWaitTime;
	}

	public String getTransOfAllCampaignsTimeTracking() {
		return transOfAllCampaignsTimeTracking;
	}

	public String getTransOfAllCampaignsTimeBefore01() {
		return transOfAllCampaignsTimeBefore01;
	}

	public String getTransOfAllCampaignsTimeBefore02() {
		return transOfAllCampaignsTimeBefore02;
	}

	public String getTransOfAllCampaignsTimeBefore03() {
		return transOfAllCampaignsTimeBefore03;
	}

	public String getTransOfAllCampaignsTimeBefore04() {
		return transOfAllCampaignsTimeBefore04;
	}

	public String getTransOfAllCampaignsThreshold() {
		return transOfAllCampaignsThreshold;
	}

	public String getTransOfAllCampaignsTitle() {
		return transOfAllCampaignsTitle;
	}

	public String getTransOfAllCampaignsDescription() {
		return transOfAllCampaignsDescription;
	}

	public String getUserInVietNamWaitTime() {
		return userInVietNamWaitTime;
	}

	public String getUserInVietNamTimeTracking() {
		return userInVietNamTimeTracking;
	}

	public String getUserInVietNamThreshold() {
		return userInVietNamThreshold;
	}

	public String getUserInVietNamTitle() {
		return userInVietNamTitle;
	}

	public String getUserInVietNamDescription() {
		return userInVietNamDescription;
	}

	public String getUserLocationWaitTime() {
		return userLocationWaitTime;
	}

	public String getUserLocationTimeTracking() {
		return userLocationTimeTracking;
	}

	public String getUserLocationThreshold() {
		return userLocationThreshold;
	}

	public String getUserLocationTitle() {
		return userLocationTitle;
	}

	public String getUserLocationDescription() {
		return userLocationDescription;
	}

	public String getUserMNOWaitTime() {
		return userMNOWaitTime;
	}

	public String getUserMNOTimeTracking() {
		return userMNOTimeTracking;
	}

	public String getUserMNOValidData() {
		return userMNOValidData;
	}

	public String getUserMNOTitle() {
		return userMNOTitle;
	}

	public String getUserMNODescription() {
		return userMNODescription;
	}

	public String getCampaignAmountTimeBefore01() {
		return campaignAmountTimeBefore01;
	}

	public String getCampaignAmountTimeBefore02() {
		return campaignAmountTimeBefore02;
	}

	public String getCampaignAmountTimeBefore03() {
		return campaignAmountTimeBefore03;
	}

	public String getCampaignAmountTimeBefore04() {
		return campaignAmountTimeBefore04;
	}

	public String getCampaignAmountTimeBefore05() {
		return campaignAmountTimeBefore05;
	}

	public String getCampaignAmountTimeBefore06() {
		return campaignAmountTimeBefore06;
	}

	public String getUserIPWaitTime() {
		return userIPWaitTime;
	}

	public String getUserIPTimeTracking() {
		return userIPTimeTracking;
	}

	public String getUserIPThreshold() {
		return userIPThreshold;
	}

	public String getUserIPTitle() {
		return userIPTitle;
	}

	public String getUserIPDescription() {
		return userIPDescription;
	}

	public String getCardMappingWaitTime() {
		return cardMappingWaitTime;
	}

	public String getCardMappingTimeTracking() {
		return cardMappingTimeTracking;
	}

	public String getCardMappingTitle() {
		return cardMappingTitle;
	}

	public String getCardMappingDescription() {
		return cardMappingDescription;
	}

	public String getCardMappingThreshold() {
		return cardMappingThreshold;
	}

	public String getCardMappingTimeBefore01() {
		return cardMappingTimeBefore01;
	}

	public String getCardMappingTimeBefore02() {
		return cardMappingTimeBefore02;
	}

	public String getCardMappingTimeBefore03() {
		return cardMappingTimeBefore03;
	}

	public String getCardMappingTimeBefore04() {
		return cardMappingTimeBefore04;
	}

	public String getAccountOnboardWaitTime() {
		return accountOnboardWaitTime;
	}

	public String getAccountOnboardTimeTracking() {
		return accountOnboardTimeTracking;
	}

	public String getAccountOnboardThreshold() {
		return accountOnboardThreshold;
	}

	public String getAccountOnboardTimeBefore01() {
		return accountOnboardTimeBefore01;
	}

	public String getAccountOnboardTimeBefore02() {
		return accountOnboardTimeBefore02;
	}

	public String getAccountOnboardTimeBefore03() {
		return accountOnboardTimeBefore03;
	}

	public String getAccountOnboardTimeBefore04() {
		return accountOnboardTimeBefore04;
	}

	public String getAccountOnboardTitle() {
		return accountOnboardTitle;
	}

	public String getAccountOnboardDescription() {
		return accountOnboardDescription;
	}

	public String getUserInVietNamNumUser() {
		return userInVietNamNumUser;
	}

	public String getUserLocationNumUser() {
		return userLocationNumUser;
	}

	public String getUserMNONumUser() {
		return userMNONumUser;
	}

	public String getCampaignAmountStatus() {
		return campaignAmountStatus;
	}

	public String getDeviceFactorsStatus() {
		return deviceFactorsStatus;
	}

	public String getTransOfAllCampaignsStatus() {
		return transOfAllCampaignsStatus;
	}

	public String getUserInVietNamStatus() {
		return userInVietNamStatus;
	}

	public String getUserLocationStatus() {
		return userLocationStatus;
	}

	public String getUserMNOStatus() {
		return userMNOStatus;
	}

	public String getUserIPStatus() {
		return userIPStatus;
	}

	public String getCardMappingStatus() {
		return cardMappingStatus;
	}

	public String getAccountOnboardStatus() {
		return accountOnboardStatus;
	}

	public String getSame2ndReceiverStatus() {
		return same2ndReceiverStatus;
	}

	public String getSame2ndReceiverWaitTime() {
		return same2ndReceiverWaitTime;
	}

	public String getSame2ndReceiverTimeTracking() {
		return same2ndReceiverTimeTracking;
	}

	public String getSame2ndReceiverThreshold() {
		return same2ndReceiverThreshold;
	}

	public String getSame2ndReceiverTitle() {
		return Same2ndReceiverTitle;
	}

	public String getSame2ndReceiverDescription() {
		return same2ndReceiverDescription;
	}

	public String getAmountPerUserStatus() {
		return AmountPerUserStatus;
	}

	public String getAmountPerUserWaitTime() {
		return AmountPerUserWaitTime;
	}

	public String getAmountPerUserTimeTracking() {
		return AmountPerUserTimeTracking;
	}

	public String getAmountPerUserThreshold() {
		return AmountPerUserThreshold;
	}

	public String getAmountPerUserTitle() {
		return AmountPerUserTitle;
	}

	public String getAmountPerUserDescription() {
		return AmountPerUserDescription;
	}

	public String getNumGroupsPerUserStatus() {
		return numGroupsPerUserStatus;
	}

	public String getNumGroupsPerUserWaitTime() {
		return numGroupsPerUserWaitTime;
	}

	public String getNumGroupsPerUserTimeTracking() {
		return numGroupsPerUserTimeTracking;
	}

	public String getNumGroupsPerUserThreshold() {
		return numGroupsPerUserThreshold;
	}

	public String getNumGroupsPerUserTitle() {
		return numGroupsPerUserTitle;
	}

	public String getNumGroupsPerUserDescription() {
		return numGroupsPerUserDescription;
	}

	public String getRejectRateLixiStatus() {
		return rejectRateLixiStatus;
	}

	public String getRejectRateLixiWaitTime() {
		return rejectRateLixiWaitTime;
	}

	public String getRejectRateLixiTimeTracking() {
		return rejectRateLixiTimeTracking;
	}

	public String getRejectRateLixiRejectNumber() {
		return rejectRateLixiRejectNumber;
	}

	public String getRejectRateLixiRejectRate() {
		return rejectRateLixiRejectRate;
	}

	public String getRejectRateLixiTitle() {
		return rejectRateLixiTitle;
	}

	public String getRejectRateLixiDescription() {
		return rejectRateLixiDescription;
	}

	public String getAutoUpdateMetricDataWaitTime() {
		return autoUpdateMetricDataWaitTime;
	}

	public String getGetAutoUpdateMetricDataTimeTracking() {
		return getAutoUpdateMetricDataTimeTracking;
	}

	public String getAutoUpdateMetricDataStatus() {
		return autoUpdateMetricDataStatus;
	}

	public String getNotA30ButOpenLixiStatus() {
		return NotA30ButOpenLixiStatus;
	}

	public String getNotA30ButOpenLixiWaitTime() {
		return NotA30ButOpenLixiWaitTime;
	}

	public String getNotA30ButOpenLixiTimeTracking() {
		return NotA30ButOpenLixiTimeTracking;
	}

	public String getNotA30ButOpenLixiThreshold() {
		return NotA30ButOpenLixiThreshold;
	}

	public String getNotA30ButOpenLixiTitle() {
		return NotA30ButOpenLixiTitle;
	}

	public String getNotA30ButOpenLixiDescription() {
		return NotA30ButOpenLixiDescription;
	}

	public String getNotA30ButSpinStatus() {
		return NotA30ButSpinStatus;
	}

	public String getNotA30ButSpinWaitTime() {
		return NotA30ButSpinWaitTime;
	}

	public String getNotA30ButSpinTimeTracking() {
		return NotA30ButSpinTimeTracking;
	}

	public String getNotA30ButSpinThreshold() {
		return NotA30ButSpinThreshold;
	}

	public String getNotA30ButSpinTitle() {
		return NotA30ButSpinTitle;
	}

	public String getNotA30ButSpinDescription() {
		return NotA30ButSpinDescription;
	}
}
