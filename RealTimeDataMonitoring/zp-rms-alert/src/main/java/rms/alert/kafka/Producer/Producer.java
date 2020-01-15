//package rms.alert.kafka.Producer;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//@EnableKafka
////@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/application.properties")
//@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/application.properties")
//public class Producer {
//
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                "10.50.1.6:9092,10.50.1.7:9092,10.50.1.8:9092");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class);
//
//        return props;
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public Sender sender() {
//        return new Sender();
//    }
//}