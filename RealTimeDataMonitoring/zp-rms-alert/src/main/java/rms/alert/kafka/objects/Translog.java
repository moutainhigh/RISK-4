package rms.alert.kafka.objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import rms.alert.data.configs.TopicConfig;
import rms.alert.utils.geoboundary.GeoBoundary;
import rms.alert.utils.geoboundary.beans.MyPoint;
import rms.alert.utils.mynumberformat.MyNumberFormat;

public class Translog {

	private String reqDate;
	private String topic;
	private String campaignID;

	private String transStatus;
	private String amount;
	private String userChargeAmount;
	private String discountAmount;
	private String isInVietNam;
	private String transID;
	private String userID;
	private String latitude;
	private String longitude;
	private String deviceFactors;
	private String deviceID;
	private String appID;
	private String pmcID;
	private String mno;
	private String userIP;
	private String requestID;
	private String mapCardSuccess;
	private String onboardUserID;
	private String profileLevel;

	private String transType;
	private String appUser;
	private String lixiFeature;
	private String lixiErrorCode;
	private String lixiSender;
	private String lixiReceiver;
	private String lixiReceiverZaloID;
	private String lixiGroupZID;
	private String lixiGroupID;
	private String lixiOpenTime;

	private String shareLixiErrorCode;
	private String shareLixiLuckyMsg;
	private String shareLixiTime;
	private String shareLixiZaloID;

	private String zpiZaloIsA30;
	private String zpiZaloTime;
	private String zpiZaloUserID;

	@Autowired
	private GeoBoundary geoBoundary;

	private void mapValueOfField(TopicConfig topicConfig) {
		if (topic.equals(topicConfig.getTopics2())) {
			//mapCard
			transID = requestID;
			if (mapCardSuccess.equals("true"))
				transStatus = "1";
			else
				transStatus = "0";
		} else if (topic.equals(topicConfig.getTopics3())) {
			//accountOnboard
			userID = onboardUserID;
			if (profileLevel.equals("0"))
				transStatus = "1";
			else
				transStatus = "0";
		} else if (topic.equals(topicConfig.getTopics4())){
			//lixiOpen
			reqDate = lixiOpenTime;
		} else if (topic.equals(topicConfig.getTopics5())){
			//shareLixi
			reqDate = shareLixiTime;
			if (shareLixiErrorCode.contains("null")){
				shareLixiErrorCode = "0";
			}
		} else if (topic.equals(topicConfig.getTopics6())){
			//checkPointA30
			reqDate = zpiZaloTime;
			userID = zpiZaloUserID;
			if (zpiZaloIsA30.equals("true")){
				transStatus = "1";
			}else{
				transStatus = "0";
			}
		} else {
			//translog
			if (transStatus.equals("1"))
				transStatus = "1";
			else
				transStatus = "0";
		}
	}
	
	public Translog(JSONObject jsonObject, String topic, TopicConfig topicConfig) {
		// Getting normal common information
		this.topic = topic;
		transID = getDataFromJsonObject(jsonObject, "transID");
		userID = getDataFromJsonObject(jsonObject, "userID");
		appID = getDataFromJsonObject(jsonObject, "appID");
		pmcID = getDataFromJsonObject(jsonObject, "pmcID");
		transStatus = getDataFromJsonObject(jsonObject, "transStatus");
		deviceID = getDataFromJsonObject(jsonObject, "deviceID");
		amount = getDataFromJsonObject(jsonObject, "amount");
		userChargeAmount = getDataFromJsonObject(jsonObject, "userChargeAmount");
		discountAmount = getDataFromJsonObject(jsonObject, "discountAmount");
		reqDate = getDataFromJsonObject(jsonObject, "reqDate");
		requestID = getDataFromJsonObject(jsonObject, "requestID");
		onboardUserID = getDataFromJsonObject(jsonObject, "userId");
		mapCardSuccess = getDataFromJsonObject(jsonObject, "isSuccessful");
		profileLevel = getDataFromJsonObject(jsonObject, "profileLevel");


		// Getting value of lixi
		transType = getDataFromJsonObject(jsonObject, "transType");
		appUser = getDataFromJsonObject(jsonObject, "appUser");
		lixiFeature = getDataFromJsonObject(jsonObject, "feature");
		lixiErrorCode = getDataFromJsonObject(jsonObject, "errorCode");
		lixiSender = getDataFromJsonObject(jsonObject, "buyerZPID");
		lixiReceiver = getDataFromJsonObject(jsonObject, "openerZPID");
		lixiReceiverZaloID = getDataFromJsonObject(jsonObject, "openerZID");
		lixiGroupZID = getDataFromJsonObject(jsonObject, "groupZID");
		lixiGroupID = getDataFromJsonObject(jsonObject, "groupID");
		lixiOpenTime = getDataFromJsonObject(jsonObject,"openTime");

		// Getting value of shareable lixi
		shareLixiErrorCode = getDataFromJsonObject(jsonObject,"error_code");
		shareLixiLuckyMsg = getDataFromJsonObject(jsonObject,"luckyMessage");
		shareLixiTime = getDataFromJsonObject(jsonObject,"time");
		shareLixiZaloID = getDataFromJsonObject(jsonObject,"zaloID");

		//Getting value of zpi_zalo_campaign_user log
		zpiZaloIsA30 = getDataFromJsonObject(jsonObject,"isA30");
		zpiZaloTime = getDataFromJsonObject(jsonObject,"time");
		zpiZaloUserID = getDataFromJsonObject(jsonObject,"zaloID");

		// Getting value of "deviceFactors"
		campaignID = getDataFromJsonObject(jsonObject, "campaignID");
		mno = getDataFromJsonObject(jsonObject, "mno");
		userIP = getDataFromJsonObject(jsonObject, "userIP");
		String deviceModel = getDataFromJsonObject(jsonObject, "deviceModel");
		String osVer = getDataFromJsonObject(jsonObject, "osVer");
		String appVer = getDataFromJsonObject(jsonObject, "appVer");
		String bankCode = getDataFromJsonObject(jsonObject, "bankcode");
		String first6CharCardNo = getDataFromJsonObject(jsonObject, "first6CardNo");
		deviceFactors = mno + '-' + userIP + '-' + deviceModel + '-' + osVer + '-' + appVer + '-' + bankCode + '-'
				+ first6CharCardNo + '-' + campaignID;
		
		// Mapping some value of field
		mapValueOfField(topicConfig);
		
		// Getting value of "isInVietNam"
		String rawLatitude = getDataFromJsonObject(jsonObject, "latitude");
		String rawLongitude = getDataFromJsonObject(jsonObject, "longitude");
		latitude = MyNumberFormat.latLngToFixed(rawLatitude);
		longitude = MyNumberFormat.latLngToFixed(rawLongitude);

		if (rawLatitude.contains("0") && rawLongitude.contains("0")) {
			isInVietNam = "-1";
		} else {
			double rawLatDoub = Double.parseDouble(rawLatitude);
			double rawLngDoub = Double.parseDouble(rawLongitude);
			MyPoint mp = new MyPoint(rawLngDoub, rawLatDoub);
			boolean result = geoBoundary.IsPointInBoundaryList(mp);
			if (result)
				isInVietNam = "1";
			else
				isInVietNam = "0";
		}

	}

	private String getDataFromJsonObject(JSONObject jsonObject, String key) {
		String res = "0";

		if (jsonObject.has(key) && jsonObject.get(key).toString().length() != 0) {
			res = jsonObject.get(key).toString();
		}

		return res;
	}

	public JSONObject toJSONObject(String type, String name, String value) {
		JSONObject jo = new JSONObject();

		if (type == "tag") {
			jo.put("type", "tag");
			jo.put("tag", name);
		} else {
			jo.put("type", "field");
			jo.put("field", name);
		}
		jo.put("value", value);

		return jo;
	}
	
	public JSONArray toJSONArray() {
		JSONArray ja = new JSONArray();

		ja.put(toJSONObject("tag", "topic", topic));
		ja.put(toJSONObject("tag", "campaignID", campaignID));
		ja.put(toJSONObject("tag", "transStatus", transStatus));

		ja.put(toJSONObject("field", "amount", amount));
		ja.put(toJSONObject("field", "userChargeAmount", userChargeAmount));
		ja.put(toJSONObject("field", "discountAmount", discountAmount));
		ja.put(toJSONObject("field", "isInVietNam", isInVietNam));
		ja.put(toJSONObject("field", "transID", transID));
		ja.put(toJSONObject("field", "userID", userID));
		ja.put(toJSONObject("field", "latitude", latitude));
		ja.put(toJSONObject("field", "longitude", longitude));
		ja.put(toJSONObject("field", "deviceFactors", deviceFactors));
		ja.put(toJSONObject("field", "deviceID", deviceID));
		ja.put(toJSONObject("field", "appID", appID));
		ja.put(toJSONObject("field", "pmcID", pmcID));
		ja.put(toJSONObject("field", "mno", mno));
		ja.put(toJSONObject("field", "userIP", userIP));

		ja.put(toJSONObject("field", "transType", transType));
		ja.put(toJSONObject("field", "appUser", appUser));
		ja.put(toJSONObject("field", "lixiFeature", lixiFeature));
		ja.put(toJSONObject("field", "lixiErrorCode", lixiErrorCode));
		ja.put(toJSONObject("field", "lixiSender", lixiSender));
		ja.put(toJSONObject("field", "lixiReceiver", lixiReceiver));
		ja.put(toJSONObject("field", "lixiReceiverZaloID", lixiReceiverZaloID));
		ja.put(toJSONObject("field", "lixiGroupID", lixiGroupID));
		ja.put(toJSONObject("field", "lixiGroupZID", lixiGroupZID));

		ja.put(toJSONObject("field", "shareLixiErrorCode", shareLixiErrorCode));
		ja.put(toJSONObject("field", "shareLixiLuckyMsg", shareLixiLuckyMsg));
		ja.put(toJSONObject("field", "shareLixiZaloID", shareLixiZaloID));

		return ja;
	}

	public String getTopic() {
		return topic;
	}

	public String getCampaignID() {
		return campaignID;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public String getAmount() {
		return amount;
	}

	public String getUserChargeAmount() {
		return userChargeAmount;
	}

	public String getDiscountAmount() {
		return discountAmount;
	}

	public String getIsInVietNam() {
		return isInVietNam;
	}

	public String getTransID() {
		return transID;
	}

	public String getUserID() {
		return userID;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getDeviceFactors() {
		return deviceFactors;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public String getAppID() {
		return appID;
	}

	public String getPmcID() {
		return pmcID;
	}

	public String getMno() {
		return mno;
	}

	public String getReqDate() {
		return reqDate;
	}

	public String getUserIP() {
		return userIP;
	}

	public String getRequestID() {
		return requestID;
	}

	public String getMapCardSuccess() {
		return mapCardSuccess;
	}

	public String getOnboardUserID() {
		return onboardUserID;
	}

	public String getProfileLevel() {
		return profileLevel;
	}

	public String getTransType() {
		return transType;
	}

	public String getAppUser() {
		return appUser;
	}

	public String getLixiFeature() {
		return lixiFeature;
	}

	public String getLixiErrorCode() {
		return lixiErrorCode;
	}

	public String getLixiSender() {
		return lixiSender;
	}

	public String getLixiReceiver() {
		return lixiReceiver;
	}

	public String getLixiGroupZID() {
		return lixiGroupZID;
	}

	public String getLixiGroupID() {
		return lixiGroupID;
	}

	@Override
	public String toString() {
		return "Translog{" +
				"reqDate='" + reqDate + '\'' +
				", topic='" + topic + '\'' +
				", campaignID='" + campaignID + '\'' +
				", transStatus='" + transStatus + '\'' +
				", amount='" + amount + '\'' +
				", userChargeAmount='" + userChargeAmount + '\'' +
				", discountAmount='" + discountAmount + '\'' +
				", isInVietNam='" + isInVietNam + '\'' +
				", transID='" + transID + '\'' +
				", userID='" + userID + '\'' +
				", latitude='" + latitude + '\'' +
				", longitude='" + longitude + '\'' +
				", deviceFactors='" + deviceFactors + '\'' +
				", deviceID='" + deviceID + '\'' +
				", appID='" + appID + '\'' +
				", pmcID='" + pmcID + '\'' +
				", mno='" + mno + '\'' +
				", userIP='" + userIP + '\'' +
				", requestID='" + requestID + '\'' +
				", mapCardSuccess='" + mapCardSuccess + '\'' +
				", onboardUserID='" + onboardUserID + '\'' +
				", profileLevel='" + profileLevel + '\'' +
				", transType='" + transType + '\'' +
				", appUser='" + appUser + '\'' +
				", lixiFeature='" + lixiFeature + '\'' +
				", lixiErrorCode='" + lixiErrorCode + '\'' +
				", lixiSender='" + lixiSender + '\'' +
				", lixiReceiver='" + lixiReceiver + '\'' +
				", lixiGroupZID='" + lixiGroupZID + '\'' +
				", lixiGroupID='" + lixiGroupID + '\'' +
				'}';
	}
}