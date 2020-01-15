package rms.alert.data.classes;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class SenderReceiverLixiOpen {
    @Column(name = "time")
    private String time;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "lixiReceiverZaloID")
    private String lixiReceiverZaloID;

    @Column(name = "amount")
    private String amount;

    @Column(name = "lixiGroupID")
    private String groupID;

    @Column(name = "lixiGroupZID")
    private String groupZID;

    public String getLixiReceiverZaloID() {
        return lixiReceiverZaloID;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getAmount() {
        return amount;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupZID() {
        return groupZID;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SenderReceiverLixiOpen{" +
                "time='" + time + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", lixiReceiverZaloID='" + lixiReceiverZaloID + '\'' +
                ", amount='" + amount + '\'' +
                ", groupID='" + groupID + '\'' +
                ", groupZID='" + groupZID + '\'' +
                '}';
    }
}
