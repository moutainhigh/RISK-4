package rms.alert.metrics.metrics.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rms.alert.data.DataManager;
import rms.alert.data.classes.DeviceFactors;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.util.*;

@Component
public class DeviceFactorsMetric implements Runnable {

	private final Logger logger = LogManager.getLogger(DeviceFactorsMetric.class);
	private HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();

	@Autowired
	private DataManager dataManager;

	@Autowired
	private Alert alert;

	@Autowired
	private MetricsConfig metricsConfig;

	public List<DeviceFactors> getMetricData() {
		return dataManager.getDeviceFactors(metricsConfig.getDeviceFactorsTimeTracking());
	}

	private void addToList(String mapKey, String value) {
		ArrayList<String> itemList = hashMap.get(mapKey);
		if (itemList == null) {
			itemList = new ArrayList<String>();
			itemList.add(value);
			hashMap.put(mapKey, itemList);
		} else {
			if (!itemList.contains(value))
				itemList.add(value);
		}
	}

	@Override
	public void run() {
		if (!metricsConfig.getDeviceFactorsStatus().equals("on")) return;
		
		Long threshold = Long.parseLong(metricsConfig.getDeviceFactorsThreshold());
		String timeTracking = metricsConfig.getDeviceFactorsTimeTracking();

		List<DeviceFactors> result = getMetricData();
		for (DeviceFactors item : result) {
			addToList(item.getDeviceFactors(), item.getUserID());
		}

		for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
			String key = entry.getKey();
			ArrayList<String> value = entry.getValue();
			if (value.size() > threshold) {
				String userIDList = value.toString();
				userIDList = userIDList.substring(1, userIDList.length() - 1);
				String title = metricsConfig.getDeviceFactorsTitle();
				String description = String.format(metricsConfig.getDeviceFactorsDescription(), 
													timeTracking, 
													threshold, 
													key, 
													value.size(), 
													userIDList);
				logger.info("DeviceFactors {} with quantity of same userID {} and threshold {} is added to alert content",
							key, userIDList, threshold);
				alert.addAlertContent(title, description, "default");
			}
		}
		
		hashMap.clear();
	}
}
