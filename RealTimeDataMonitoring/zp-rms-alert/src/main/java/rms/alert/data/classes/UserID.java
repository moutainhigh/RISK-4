package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class UserID {

    @Column(name = "campaignID")
    private String campaignID;

    @Column(name = "userID")
    public String userID;

    public String getCampaignID() {
        return campaignID;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "UserID{" +
                "campaignID='" + campaignID + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
