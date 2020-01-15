package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import rms.alert.utils.datetime.DateTime;

@Measurement(name = "")
public class UserIP {

    @Column(name = "time")
    private String time;

    @Column(name = "campaignID")
    private String campaignID;

    @Column(name = "userIP")
    private String userIP;

    public String getUserIP() {
        return userIP;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "UserIP{" +
                "time='" + time + '\'' +
                ", campaignID='" + campaignID + '\'' +
                ", userIP='" + userIP + '\'' +
                '}';
    }
}
