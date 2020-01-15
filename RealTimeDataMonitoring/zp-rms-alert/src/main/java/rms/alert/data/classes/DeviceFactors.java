package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class DeviceFactors {
	
	@Column(name = "deviceFactors")
    private String deviceFactors;

    @Column(name = "userID")
    private String userID;

    public String getDeviceFactors() {
        return deviceFactors;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "DeviceFactors{" +
                "deviceFactors='" + deviceFactors + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
