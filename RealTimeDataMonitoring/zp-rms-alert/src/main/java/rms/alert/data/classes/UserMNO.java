package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class UserMNO {

	@Column(name = "campaignID")
	private String campaignID;

	@Column(name = "userID")
	private String userID;
	
	@Column(name = "mno")
	private String mno;

	public String getUserID() {
		return userID;
	}

	public String getMno() {
		return mno;
	}

	public String getCampaignID() {
		return campaignID;
	}

	@Override
	public String toString() {
		return "UserMNO{" +
				"campaignID='" + campaignID + '\'' +
				", userID='" + userID + '\'' +
				", mno='" + mno + '\'' +
				'}';
	}
}
