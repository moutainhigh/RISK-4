package rms.alert.data.configs;

import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Pong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/influxdb.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/influxdb.properties")
//@PropertySource("file:${HOME}/conf/influxdb.properties")
public class InfluxDBConfig {

	@Value("${influx.databaseURL}")
	private String databaseURL;

	@Value("${influx.username}")
	private String userName;

	@Value("${influx.password}")
	private String password;

	@Value("${influx.databaseName}")
	private String databaseName;

	@Value("${influx.timeDuration}")
	private String timeDuration;

	@Value("${influx.timeShard}")
	private String timeShard;

	@Value("${influx.replication}")
	private String replication;

	@Value("${influx.retentionPolicyName}")
	private String retentionPolicyName;

	@Value("${influx.measurementName}")
	private String measurementName;

	public static InfluxDB influxDB;
	public static BatchPoints batchPoints;
	private static final Logger logger = LogManager.getLogger();

	@Bean
	public void connect() {
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES)
				.readTimeout(10, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES);

		influxDB = InfluxDBFactory.connect(databaseURL, userName, password, okHttpClientBuilder);
		Pong response = influxDB.ping();

		if (response.getVersion().equalsIgnoreCase("unknown")) {
			logger.error("Connect to InfluxDB fail");
			return;
		} else {
			influxDB.enableGzip();
			influxDB.setRetentionPolicy(retentionPolicyName);
			influxDB.setDatabase(databaseName);
			logger.info(
					"DatabaseURL {} - Database name {} - Measurement name {} - Retention policy name {} - Time duration {} - Time shard {}",
					databaseURL, databaseName, measurementName, retentionPolicyName, timeDuration, timeShard);
		}
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getTimeDuration() {
		return timeDuration;
	}

	public String getTimeShard() {
		return timeShard;
	}

	public String getReplication() {
		return replication;
	}

	public String getRetentionPolicyName() {
		return retentionPolicyName;
	}

	public String getMeasurementName() {
		return measurementName;
	}

}
