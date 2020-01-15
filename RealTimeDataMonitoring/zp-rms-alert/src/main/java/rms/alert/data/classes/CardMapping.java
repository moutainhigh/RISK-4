package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class CardMapping {

    @Column(name = "count")
    private Long numCardMapping;

    public Long getNumCardMapping() {
        return numCardMapping;
    }

    @Override
    public String toString() {
        return "CardMapping{" +
                "numCardMapping=" + numCardMapping +
                '}';
    }
}
