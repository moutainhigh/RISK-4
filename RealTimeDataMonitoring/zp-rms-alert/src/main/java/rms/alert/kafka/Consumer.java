package rms.alert.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;
import rms.alert.data.DataManager;
import rms.alert.data.configs.TopicConfig;
import rms.alert.kafka.objects.Translog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

@Service
@EnableKafka
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/application.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/application.properties")
public class Consumer {

    private static final Logger logger = LogManager.getLogger();
    public static final int NUM_OF_THREAD = 10;
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    @Autowired
    private DataManager dataManager;

    // ---------------------------------
    // Kafka 1
    // ---------------------------------
    @Value("${spring.kafka.consumer1.bootstrap-servers}")
    private String bootstrapServers1;

    @Value("${spring.kafka.consumer1.group-id}")
    private String groupID1;

    @Value("${spring.kafka.consumer1.topics}")
    private String topics1;

    // ---------------------------------
    // Kafka 2
    // ---------------------------------
    @Value("${spring.kafka.consumer2.bootstrap-servers}")
    private String bootstrapServers2;

    @Value("${spring.kafka.consumer2.group-id}")
    private String groupID2;

    @Value("${spring.kafka.consumer2.topics}")
    private String topics2;

    // ---------------------------------
    // Kafka 3
    // ---------------------------------
    @Value("${spring.kafka.consumer3.bootstrap-servers}")
    private String bootstrapServers3;

    @Value("${spring.kafka.consumer3.group-id}")
    private String groupID3;

    @Value("${spring.kafka.consumer3.topics}")
    private String topics3;

    // ---------------------------------
    // Kafka 4
    // ---------------------------------
    @Value("${spring.kafka.consumer4.bootstrap-servers}")
    private String bootstrapServers4;

    @Value("${spring.kafka.consumer4.group-id}")
    private String groupID4;

    @Value("${spring.kafka.consumer4.topics}")
    private String topics4;

    // ---------------------------------
    // Kafka 5
    // ---------------------------------
    @Value("${spring.kafka.consumer5.bootstrap-servers}")
    private String bootstrapServers5;

    @Value("${spring.kafka.consumer5.group-id}")
    private String groupID5;

    @Value("${spring.kafka.consumer5.topics}")
    private String topics5;

    // ---------------------------------
    // Kafka 6
    // ---------------------------------
    @Value("${spring.kafka.consumer6.bootstrap-servers}")
    private String bootstrapServers6;

    @Value("${spring.kafka.consumer6.group-id}")
    private String groupID6;

    @Value("${spring.kafka.consumer6.topics}")
    private String topics6;

    @Autowired
    TopicConfig topicConfig;

    /**
     * Set up test properties for an {@code <Integer, String>} consumer.
     * @param brokersCommaSep the bootstrapServers property (comma separated servers).
     * @param group the group id.
     * @param autoCommit the auto commit.
     * @return the properties.
     */
    private static Map<String, Object> consumerProps(String brokersCommaSep, String group) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokersCommaSep);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // Start brokers without using the "@KafkaListener" annotation
    private void listeningKafka(String brokersCommaSep, String group, String topic) {
        Map<String, Object> consumerProps = consumerProps(brokersCommaSep, group);
        DefaultKafkaConsumerFactory<Object, Object> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProperties = new ContainerProperties(topic);

        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(cf, containerProperties);
        container.setConcurrency(2);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            queue.add(() -> { processKafka(record.value(), topic); });
        });
        container.start();
    }

    private void processKafka(String message, String topic) {
        JSONObject jsonMessage = new JSONObject(message);
        Translog translog = new Translog(jsonMessage, topic, topicConfig);
        boolean result = dataManager.saveData(translog.getReqDate(), translog.toJSONArray());
        if (!result) {
            queue.add(() -> { processKafka(message, topic); });
        }
    }

    private void runningThreadPool() {
        try
        {
            ExecutorService ES = Executors.newFixedThreadPool(NUM_OF_THREAD);
            for (int i=0; i<NUM_OF_THREAD; ++i) {
                ES.execute(() -> {
                    while (true) {
                        try {
                            queue.take().run();
                        }
                        catch (InterruptedException e) {
                            logger.error(e.getMessage());
                        }

                    }
                });
            }
        } catch (IllegalArgumentException | NullPointerException | RejectedExecutionException e) {
            logger.error("Threadpool for consumer don't run - {}", e.getMessage());
        }
    }

    @Bean
    private void runningKafka() {
        // Listening Kafka
        listeningKafka(bootstrapServers1, groupID1, topics1);
//        listeningKafka(bootstrapServers2, groupID2, topics2);
//        listeningKafka(bootstrapServers3, groupID3, topics3);
        listeningKafka(bootstrapServers4, groupID4, topics4);
        listeningKafka(bootstrapServers5, groupID5, topics5);
        listeningKafka(bootstrapServers6, groupID6, topics6);

        // Running threadpool for processing Kafka
        runningThreadPool();
    }
}