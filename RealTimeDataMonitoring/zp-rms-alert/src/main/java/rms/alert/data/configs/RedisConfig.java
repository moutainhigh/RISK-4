package rms.alert.data.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/redis.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/redis.properties")
public class RedisConfig {
//    @Value("${redis.host}")
//    private String redisHost;
//
//    @Value("${redis.port}")
//    private int redisPort;
//
//    @Value("${redis.password}")
//    private String redisPassword;

    @Value("${redis.nodeAddress}")
    private String nodeAddress;

    private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        List<String> clusterNodes = new ArrayList<>();
        String[] nodes = nodeAddress.split(",");
        for (String item : nodes){
            clusterNodes.add(item);
        }
        RedisClusterConfiguration redis = new RedisClusterConfiguration(clusterNodes);
        logger.info("Redis connect cluster..."+clusterNodes.toString());
        return new LettuceConnectionFactory(redis);
    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        // Tạo Standalone Connection tới Redis
//        logger.info("Redis connect...");
//        RedisStandaloneConfiguration redis = new RedisStandaloneConfiguration();
//        redis.setPort(redisPort);
//        redis.setHostName(redisHost);
//        redis.setPassword(redisPassword);
//        return new LettuceConnectionFactory(redis);
//    }


    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        logger.info("Redis template....");
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}