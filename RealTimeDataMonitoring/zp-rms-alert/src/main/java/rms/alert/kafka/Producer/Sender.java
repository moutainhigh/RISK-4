//package rms.alert.kafka.Producer;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//
//public class Sender {
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void send(String payload) {
//        kafkaTemplate.send("cong-shareable-lixi1",payload);
//    }
//}