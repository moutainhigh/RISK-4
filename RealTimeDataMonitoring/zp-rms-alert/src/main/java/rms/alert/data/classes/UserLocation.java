package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class UserLocation {

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "userID")
    private String userID;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
