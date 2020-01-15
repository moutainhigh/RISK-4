package rms.alert.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
//import rms.alert.kafka.Producer.Sender;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.interval.SetIntervalThreadPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class IntervalCalculation  implements CommandLineRunner {
    private SetIntervalThreadPool siTP = new SetIntervalThreadPool();

    @Autowired
    private DataManager dataManager;

//    @Autowired
//    private Sender sender;

    @Autowired MetricsConfig metricsConfig;

    // Load html
    @Autowired
    private Environment environment;

    private String getConfigPath() {
        try {
            return environment.getProperty("app.path").trim();
        } catch (Exception e) {
            return ".";
        }
    }

    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
        }
        return contentBuilder.toString();
    }

    @Override
    public void run(String... args) throws Exception {
//        //demo load data from open lixi from file txt
//        String data = readLineByLineJava8(getConfigPath() + "/conf/shareable-lixi.txt");
//        String[] frames = data.split(";");
//        for (String item:frames){
//            sender.send(item);
//        }
//        String data = readLineByLineJava8(getConfigPath() + "/conf/zalo-checkpoint.txt");
//        String[] frames = data.split(";");
//        for (String item:frames){
//            sender.send(item);
//            System.out.println(item);
//        }

        siTP.startInterval(metricsConfig.getAutoUpdateMetricDataWaitTime(), dataManager);
    }
}
