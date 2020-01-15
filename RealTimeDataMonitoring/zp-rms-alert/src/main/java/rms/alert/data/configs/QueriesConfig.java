package rms.alert.data.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/queries.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/queries.properties")
//@PropertySource("file:${HOME}/conf/queries.properties")
public class QueriesConfig {

	@Value("${query.current.campaignAmount}")
	private String currentCampaignAmountQuery;

	@Value("${query.before.campaignAmount}")
	private String beforeCampaignAmountQuery;

	@Value("${query.current.deviceFactors}")
	private String currentDeviceFactorsQuery;

	@Value("${query.current.deviceFactors.userIDs}")
	private String currentDeviceFactorsQueryUserIDs;

	@Value("${query.current.transOfAllCampaigns}")
	private String currentTransOfAllCampaignsQuery;

	@Value("${query.before.transOfAllCampaigns}")
	private String beforeTransOfAllCampaignsQuery;

	@Value("${query.current.userInVietNam}")
	private String currentUserInVietNamQuery;

	@Value("${query.current.userLocation}")
	private String currentUserLocationQuery;

	@Value("${query.current.userLocation.userIDs}")
	private String currentUserLocationQueryUserIDs;

	@Value("${query.current.userMNO}")
	private String currentUserMNOQuery;

	@Value("${query.current.userIP}")
	private String currentUserIPQuery;

	@Value("${query.current.cardMapping}")
	private String currentCardMapping;

	@Value("${query.before.cardMapping}")
	private String beforeCardMapping;

	@Value("${query.current.accountOnboard}")
	private String currentAccountOnboard;

	@Value("${query.before.accountOnboard}")
	private String beforeAccountOnboard;

	@Value("${query.current.tetLixiOpen}")
	private String currentTetLixiOpen;

	@Value("${query.current.tetTranslog}")
	private String currentTetTranslog;

	@Value("${query.current.shareLixi}")
	private String currentShareLixi;

	@Value("${query.current.zpiZaloIsNotA30}")
	private String currentZpiZaloIsNotA30;

	public String getCurrentZpiZaloIsNotA30() {
		return currentZpiZaloIsNotA30;
	}

	public String getCurrentCampaignAmountQuery() {
		return currentCampaignAmountQuery;
	}

	public String getBeforeCampaignAmountQuery() {
		return beforeCampaignAmountQuery;
	}

	public String getCurrentDeviceFactorsQuery() {
		return currentDeviceFactorsQuery;
	}

	public String getCurrentTransOfAllCampaignsQuery() {
		return currentTransOfAllCampaignsQuery;
	}

	public String getBeforeTransOfAllCampaignsQuery() {
		return beforeTransOfAllCampaignsQuery;
	}

	public String getCurrentUserInVietNamQuery() {
		return currentUserInVietNamQuery;
	}

	public String getCurrentUserLocationQuery() {
		return currentUserLocationQuery;
	}

	public String getCurrentUserMNOQuery() {
		return currentUserMNOQuery;
	}

	public String getCurrentDeviceFactorsQueryUserIDs() {
		return currentDeviceFactorsQueryUserIDs;
	}

	public String getCurrentUserLocationQueryUserIDs() {
		return currentUserLocationQueryUserIDs;
	}

	public String getCurrentUserIPQuery() {
		return currentUserIPQuery;
	}

	public String getCurrentCardMapping() {
		return currentCardMapping;
	}

	public String getBeforeCardMapping() {
		return beforeCardMapping;
	}

	public String getCurrentAccountOnboard() {
		return currentAccountOnboard;
	}

	public String getBeforeAccountOnboard() {
		return beforeAccountOnboard;
	}

	public String getCurrentTetLixiOpen() {
		return currentTetLixiOpen;
	}

	public String getCurrentTetTranslog() {
		return currentTetTranslog;
	}

	public String getCurrentShareLixi() {
		return currentShareLixi;
	}
}
