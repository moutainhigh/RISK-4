package rms.alert.data.classes;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "")
public class ShareLixi {

    @Column(name = "userID")
    private String userID;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "shareLixiErrorCode")
    private String shareLixiErrorCode;

    @Column(name = "shareLixiLuckyMsg")
    private String shareLixiLuckyMsg;

    @Column(name = "shareLixiZaloID")
    private String shareLixiZaloID;

    public String getShareLixiZaloID() {
        return shareLixiZaloID;
    }

    public String getUserID() {
        return userID;
    }

    public Long getAmount() {
        return amount;
    }

    public String getShareLixiErrorCode() {
        return shareLixiErrorCode;
    }

    public String getShareLixiLuckyMsg() {
        return shareLixiLuckyMsg;
    }

    @Override
    public String toString() {
        return "ShareLixi{" +
                "userID='" + userID + '\'' +
                ", amount=" + amount +
                ", shareLixiErrorCode='" + shareLixiErrorCode + '\'' +
                ", shareLixiLuckyMsg='" + shareLixiLuckyMsg + '\'' +
                ", shareLixiZaloID='" + shareLixiZaloID + '\'' +
                '}';
    }
}
