package rms.alert.data.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/application.properties")
public class TopicConfig {
    @Value("${spring.kafka.consumer1.topics}")
    private String topics1;

    @Value("${spring.kafka.consumer2.topics}")
    private String topics2;

    @Value("${spring.kafka.consumer3.topics}")
    private String topics3;

    @Value("${spring.kafka.consumer4.topics}")
    private String topics4;

    @Value("${spring.kafka.consumer5.topics}")
    private String topics5;

    @Value("${spring.kafka.consumer6.topics}")
    private String topics6;

    public String getTopics1() {
        return topics1;
    }

    public String getTopics2() {
        return topics2;
    }

    public String getTopics3() {
        return topics3;
    }

    public String getTopics4() {
        return topics4;
    }

    public String getTopics5() {
        return topics5;
    }

    public String getTopics6() {
        return topics6;
    }
}