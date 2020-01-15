package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class UserInVietNam {

    @Column(name = "campaignID")
    private String campaignID;

    @Column(name = "userID")
    private String userID;

    @Column(name = "isInVietNam")
    private String isInVietNam;

    public String getCampaignID() {
        return campaignID;
    }

    public String getUserID() {
        return userID;
    }

    public String getIsInVietNam() {
        return isInVietNam;
    }

    @Override
    public String toString() {
        return "UserInVietNam{" +
                "campaignID='" + campaignID + '\'' +
                ", userID='" + userID + '\'' +
                ", isInVietNam='" + isInVietNam + '\'' +
                '}';
    }
}
