package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class AccountOnboard {

    @Column(name = "count")
    public Long numAccountOnboard;

    public Long getNumAccountOnboard() {
        return numAccountOnboard;
    }

    @Override
    public String toString() {
        return "AccountOnboard{" +
                "numAccountOnboard=" + numAccountOnboard +
                '}';
    }
}
