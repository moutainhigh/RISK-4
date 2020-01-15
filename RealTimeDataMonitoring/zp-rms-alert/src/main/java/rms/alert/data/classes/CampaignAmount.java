package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class CampaignAmount {

	@Column(name = "campaignID")
    public String campaignID;

    @Column(name = "sum")
    public Long sumDiscount;
    
    public String getCampaignID() {
        return campaignID;
    }

    public Long getSumDiscount() {
        return sumDiscount;
    }

	@Override
	public String toString() {
		return "CampaignAmount [campaignID=" + campaignID + ", sumDiscount=" + sumDiscount + "]";
	}
	
}