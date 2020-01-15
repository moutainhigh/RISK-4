package rms.alert.data.repository;

import rms.alert.data.configs.InfluxDBConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class InfluxRepo {

	private final Logger logger = LogManager.getLogger(InfluxRepo.class);

	@Autowired
	private InfluxDBConfig influxConfig;

	@Autowired
	private InfluxDBResultMapper influxDBResultMapper;

	private boolean writePointToDB(Point pt, String transID) {
		try {
			InfluxDBConfig.influxDB.write(influxConfig.getDatabaseName(), influxConfig.getRetentionPolicyName(), pt);
		} catch (Exception e) {
			logger.error("Write fail transID: {}, trying again - {}. ", transID, e.getMessage());
			return false;
		}
		return true;
	}

	private boolean isLongType(String fieldName) {
		switch (fieldName) {
		case "amount":
		case "userChargeAmount":
		case "discountAmount": {
			return true;
		}
		default:
			return false;
		}
	}

	public boolean saveData(String reqDate, JSONArray ja) {
		String transID = "";
		Builder point = Point.measurement(influxConfig.getMeasurementName());
		point.time(Long.parseLong(reqDate), TimeUnit.MILLISECONDS);
		
		for (int i = 0; i < ja.length(); ++i) {
			JSONObject obj = ja.getJSONObject(i);
			
			if (obj.get("type") == "tag") {
				String tagName = obj.get("tag").toString();
				String tagValue = obj.get("value").toString();
				point.tag(tagName, tagValue);
			} else {
				String fieldName = obj.get("field").toString();
				String fieldValue = obj.get("value").toString();
				if (isLongType(fieldName) == true) {
					point.addField(fieldName, Long.parseLong(fieldValue));
				} else {
					point.addField(fieldName, fieldValue);
				}
				if (fieldName == "transID") transID = fieldValue;
			}
		}
		
		return this.writePointToDB(point.build(), transID);
	}

	public <T> List<T> queryDatabase(String queryString, Class<T> classType) {
		try {
			Query query = new Query(queryString, influxConfig.getDatabaseName());
			QueryResult queryResult = InfluxDBConfig.influxDB.query(query);
			List<T> result = influxDBResultMapper.toPOJO(queryResult, classType);
			return result;
		} catch (Exception e) {
			logger.error("Query fail - {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
