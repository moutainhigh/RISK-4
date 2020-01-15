package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class TransOfAllCampaigns {

	@Column(name = "campaignID")
	private String campaignID;

	@Column(name = "count")
    private Long numTrans;

	public String getCampaignID() {
		return campaignID;
	}

	public Long getNumTrans() {
        return numTrans;
    }

	@Override
	public String toString() {
		return "TransOfAllCampaigns{" +
				"campaignID='" + campaignID + '\'' +
				", numTrans=" + numTrans +
				'}';
	}
}
