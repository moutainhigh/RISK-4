package rms.alert.data.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RedisRepo {
    @Autowired
    private RedisTemplate template;

    private final String redisNumUserLatLng = "rms_alert_numUserLatLng";
    private final String redisLixiSame2ndReceiver = "rms_alert_same2ndReceiver";
    private final String redisLixiAmountPerUser = "rms_alert_amountPerUser";
    private final String redisLixiNumGroupsPerUser = "rms_alert_numGroupsPerUser";

    private final String redisLixiRejectRate = "rms_alert_rejectRateLixi";
    private final String redisLixiRejectRate_RejectRateTurn = "rejectRateTurn";
    private final String redisLixiRejectRate_RejectRateUser = "rejectRateUser";

    public Map getAllLocationWithNumUserCurrent() {
        Map map = template.opsForHash().entries(redisNumUserLatLng);
        return map;
    }

    public void writeNumUserAtLocation(String latLng, int numUser) {
        template.opsForHash().put(redisNumUserLatLng, latLng, String.valueOf(numUser));
    }

    public void writeListSenderOfReceiver(String receiver, List<String> sender, String totalAmount){
        String listSender = sender.toString();
        listSender = listSender.substring(1,listSender.length()-1);
        listSender+=":"+totalAmount;
        template.opsForHash().put(redisLixiSame2ndReceiver,receiver,listSender);
    }

    public Map getAllListSenderOfReceiver(){
        Map<String, String> map = template.opsForHash().entries(redisLixiSame2ndReceiver);
        return map;
    }

    public void writeAmountPerUser(String user, String amount, List<String> groupID, List<String> groupZID){
        String value = "";
        String strGroupID = groupID.toString();
        String strGroupZID = groupZID.toString();
        strGroupID = strGroupID.substring(1,strGroupID.length()-1);
        strGroupZID = strGroupZID.substring(1,strGroupZID.length()-1);
        value = amount+":"+strGroupID+":"+strGroupZID;
        template.opsForHash().put(redisLixiAmountPerUser,user,value);
    }

    public Map getAllAmountPerUser(){
        Map<String, String> map = template.opsForHash().entries(redisLixiAmountPerUser);
        return map;
    }

    public void writeNumGroupsPerUser(String user, ArrayList<String> firstMemGroups, ArrayList<String> allGroups) {
        String value = "";
        String strFirstMemGroups = firstMemGroups.toString();
        String strAllGroups = allGroups.toString();
        strFirstMemGroups = strFirstMemGroups.substring(1,strFirstMemGroups.length()-1);
        strAllGroups = strAllGroups.substring(1,strAllGroups.length()-1);
        value = strFirstMemGroups+":"+strAllGroups;
        template.opsForHash().put(redisLixiNumGroupsPerUser,user,value);
    }

    public Map getAllNumGroupsPerUser(){
        Map<String, String> map = template.opsForHash().entries(redisLixiNumGroupsPerUser);
        return map;
    }

    public String getRejectRateTurn(){
        Map<String, String> map = template.opsForHash().entries(redisLixiRejectRate);
        return map.get(redisLixiRejectRate_RejectRateTurn);
    }

    public void writeRejectRateTurn(String rejectRate){
        template.opsForHash().put(redisLixiRejectRate,redisLixiRejectRate_RejectRateTurn,rejectRate);
    }

    public String getRejectRateUser(){
        Map<String, String> map = template.opsForHash().entries(redisLixiRejectRate);
        return map.get(redisLixiRejectRate_RejectRateUser);
    }

    public void writeRejectRateUser(String rejectRate){
        template.opsForHash().put(redisLixiRejectRate,redisLixiRejectRate_RejectRateUser,rejectRate);
    }

//    void demo(){
//          template.opsForValue().set("aKey","aValue");
//          template.expireAt("aKey",Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));

//        // Set giá trị của key "loda" là "hello redis"
//        template.opsForValue().set("loda","hello world");
//        // In ra màn hình Giá trị của key "loda" trong Redis
//        System.out.println("Value of key loda: "+template.opsForValue().get("loda"));
//
//        List<String> list = new ArrayList<>();
//        list.add("1");
//        list.add("2");
//        list.add("3");
//        list.add("4");
//        template.opsForHash().put("test", "map", list.toString());
//        template.opsForHash().put("test", "isAdmin", true);
//        Map<String, String> map = template.opsForHash().entries("test");
//        System.out.println(map.get("map")); // [1, 2, 3, 4]
//        System.out.println(map.get("isAdmin") instanceof String); // true
//        System.out.println(template.opsForHash().entries("test")); // {a=[1, 2, 3, 4], isAdmin=true}
//    }
//
//    void cacheUserSameDeviceID(String datetime, String deviceID){
//        List<String> listUserID = new ArrayList<>();
//        listUserID.add("1");
//        listUserID.add("2");
//        listUserID.add("3");
//        listUserID.add("4");
//
//        String key = "deviceID:"+datetime+":"+deviceID;
//        String field = "listUserID";
//
//        template.opsForHash().put(key, field, listUserID.toString());
//
//        Map<String, String> map = template.opsForHash().entries(key);
//        System.out.println(map.get(field));
//    }

}