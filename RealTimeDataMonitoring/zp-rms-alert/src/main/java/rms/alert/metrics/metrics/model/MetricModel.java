package rms.alert.metrics.metrics.model;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.jpmml.evaluator.visitors.DefaultVisitorBattery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import rms.alert.data.DataManager;
import rms.alert.data.classes.DeviceFactors;
import rms.alert.data.classes.TransOfAllCampaigns;
import rms.alert.data.classes.UserIP;
import rms.alert.data.classes.UserInVietNam;
import rms.alert.data.classes.UserLocation;
import rms.alert.data.classes.UserMNO;
import rms.alert.metrics.alert.Alert;
import rms.alert.metrics.metrics.configs.MetricsConfig;
import rms.alert.utils.concurrentproc.ConcurrentProc;

@Component
public class MetricModel implements Runnable {

	private Evaluator evaluator = null;
	private static final Logger logger = LogManager.getLogger();
	
	private static final String timeTracking = "30m";
	private static final String timeBefore01 = "1w";
	private static final String timeBefore02 = "2w";
	private static final String timeBefore03 = "3w";
	private static final String timeBefore04 = "4w";
	
	private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	private static List<List<TransOfAllCampaigns>> listTimeBefores = new ArrayList<>();
    private static List<TransOfAllCampaigns> listTimeTracking = new ArrayList<>();
    
    private static String max_transaction_perc_increase_mailContent = "";
    private static String max_location_totalUser_mailContent = "";
    private static String total_location_outsidevn_mailContent = "";
    private static String max_manyfactor_totalUser_mailContent = "";
    private static String min_gap_reqDate_userip_mailContent = "";
    private static String total_VN_mno_mailContent = "";
	
	@Autowired
    private DataManager dataManager;
	
	@Autowired
	private Environment environment;
	
	@Autowired
    private MetricsConfig metricsConfig;
	
	@Autowired
    private Alert alert;

	private String getConfigPath() {
		try {
			return environment.getProperty("app.path").trim();
		} catch (Exception e) {
			return ".";
		}
	}
	
	@Bean
	private void buildEvaluator() {
		try {
			String filePath = getConfigPath() + "/conf/model/rf_alert_pmml.pmml";
			evaluator = new LoadingModelEvaluatorBuilder()
						.setLocatable(false)
						.setVisitors(new DefaultVisitorBattery())
						.load(new File(filePath))
						.build();

			evaluator.verify();
			logger.info("Read done metric model");
		} catch (IOException e) {
			logger.error("Error model: {}", e.getMessage());
		} catch (SAXException e) {
			logger.error("Error model: {}", e.getMessage());
		} catch (JAXBException e) {
			logger.error("Error model: {}", e.getMessage());
		}
	}
	
	private boolean isValidMNO(List<String> ValidMNOList, String mno) {
        mno = mno.trim().toLowerCase();
        
        for (String str : ValidMNOList) {
            str = str.trim().toLowerCase();
            if (str.equals("452")) {
                if (mno.length() < 3) continue;
                String mnoTmp = mno.substring(0, 3);
                if (mnoTmp.equals(str)) return true;
            } else if (mno.contains(str)) return true;
        }
        return false;
    }
	
	private void addToList(HashMap<String, ArrayList<String>> HM, String mapKey, String value, boolean allowDuplication){
        ArrayList<String> itemList = HM.get(mapKey);
        if (itemList == null){
            itemList = new ArrayList<String>();
            itemList.add(value);
            HM.put(mapKey, itemList);
        } else{
            if (allowDuplication) itemList.add(value);
            else {
                if (!itemList.contains(value)) itemList.add(value);
            }
        }
    }
	
	private double computePercentageOfTransIncrease() {
		double percentResult = 0;
        
		// Count current transaction
        double countCurrentTrans = 0;
        for (TransOfAllCampaigns item : listTimeTracking) {
        	countCurrentTrans += item.getNumTrans().doubleValue();
        }
        
		// Average all before transactions
        double avgBeforeTrans = 0;
        List<Long> numTrans = new ArrayList<>();
        
        for (List<TransOfAllCampaigns> transOfAllCampaignsList : listTimeBefores) {
            if (transOfAllCampaignsList.size() > 0) {
                numTrans.add(transOfAllCampaignsList.get(0).getNumTrans());
            }
        }
        
        if (numTrans.size() != 0) {
        	for (Long ele : numTrans) {
        		avgBeforeTrans += ele.doubleValue();
        	}
        	avgBeforeTrans = avgBeforeTrans / numTrans.size();
        	percentResult = (countCurrentTrans - avgBeforeTrans) / avgBeforeTrans;
        }
        
        max_transaction_perc_increase_mailContent = "<br>&emsp;&emsp;+ Total transactions (timeTracking): " + countCurrentTrans;
        max_transaction_perc_increase_mailContent += "<br>&emsp;&emsp;+ Average transactions (all timeBefore): " + avgBeforeTrans;
        max_transaction_perc_increase_mailContent += "<br>&emsp;&emsp;+ Percentage of increasing transaction: " + percentResult;
        
        return percentResult;
	}
	
	@Override
	public void run() {
		// Check some condition for running MetricModel or not
		if (!metricsConfig.getMetricModelStatus().equals("on")) return;
		if (evaluator == null) return;
		
		// Declare necessary variable
		HashMap<String, Object> data = new HashMap<>();
		
		// Field: max_transaction_perc_increase
		queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore01 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore01);
            listTimeBefores.add(listTimeBefore01);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore02 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore02);
            listTimeBefores.add(listTimeBefore02);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore03 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore03);
            listTimeBefores.add(listTimeBefore03);
        });

        queue.add(() -> {
            List<TransOfAllCampaigns> listTimeBefore04 = dataManager.getTransOfAllCampaigns(timeTracking, timeBefore04);
            listTimeBefores.add(listTimeBefore04);
        });
        
        queue.add(() -> {
        	listTimeTracking = dataManager.getTransOfAllCampaigns(timeTracking);
        });
		
		// Field: max_location_totalUser
        queue.add(() -> {
        	List<UserLocation> result = dataManager.getNumUserOnEachLocation(timeTracking);
        	HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
        	
        	for (UserLocation item : result) {
                addToList(groupMap, item.getLatitude() + ", " + item.getLongitude(), item.getUserID(), true);
            }
        	
        	int maxTotalUsers = 0;
			max_location_totalUser_mailContent = "";
        	for (Entry<String, ArrayList<String>> ele : groupMap.entrySet()) {
        		ArrayList<String> arrValue = ele.getValue();
        		int size = arrValue.size();
        		if (maxTotalUsers < size) {
        			maxTotalUsers = size;
        			
        			max_location_totalUser_mailContent = "<br>&emsp;&emsp;+ Lat, Long: " + ele.getKey();
        			max_location_totalUser_mailContent += "<br>&emsp;&emsp;+ UserID List: ";
        			String userIDList = arrValue.toString();
        			userIDList = userIDList.substring(1, userIDList.length() - 1);
        			max_location_totalUser_mailContent += userIDList;
        		}
        	}

			max_location_totalUser_mailContent += "<br>&emsp;&emsp;+ Total users: " + maxTotalUsers;
        	data.put("max_location_totalUser", maxTotalUsers);
        });
		
		// Field: total_location_outsidevn
        queue.add(() -> {
        	List<UserInVietNam> result = dataManager.getUserInVietNam(timeTracking);
        	HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
        	
        	for (UserInVietNam item : result) {
                addToList(groupMap, item.getCampaignID(), item.getIsInVietNam() + "," + item.getUserID(), false);
            }
        	
        	int count = 0;
        	total_location_outsidevn_mailContent = "";
        	for(Entry<String, ArrayList<String>> ele : groupMap.entrySet()) {
                String key = ele.getKey();
                ArrayList<String> arrValue = ele.getValue();
                
                int tempCount = 0;
                for (String item : arrValue){
                    String[] arrRes = item.split(",");
                    String isInVN = arrRes[0];
                    String userID = arrRes[1];
                    if (isInVN.equals("0"))
                    {
                    	tempCount += 1;
                    	if (tempCount == 1) {
                    		total_location_outsidevn_mailContent += "<br>&emsp;&emsp;+ CampaignID: " + key + " - UserID List: ";
                    	}
                    	total_location_outsidevn_mailContent += userID + ", ";
                    }
                }
                
                if (tempCount != 0) {
                	count += tempCount;
                	total_location_outsidevn_mailContent = total_location_outsidevn_mailContent.substring(0, total_location_outsidevn_mailContent.length() - 2);
                }
            }
        	
        	total_location_outsidevn_mailContent += "<br>&emsp;&emsp;+ Total transactions outside VN: " + count;
        	data.put("total_location_outsidevn", count);
        });
		
		// Field: max_manyfactor_totalUser
        queue.add(() -> {
        	List<DeviceFactors> result = dataManager.getDeviceFactors(timeTracking);
    		HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
        	
        	for (DeviceFactors item : result) {
                addToList(groupMap, item.getDeviceFactors(), item.getUserID(), false);
            }
        	
        	int maxTotalUsers = 0;
        	for (Entry<String, ArrayList<String>> ele : groupMap.entrySet()) {
        		ArrayList<String> arrValue = ele.getValue();
        		int size = arrValue.size();
        		if (maxTotalUsers < size) {
        			maxTotalUsers = size;
        			
        			max_manyfactor_totalUser_mailContent = "<br>&emsp;&emsp;+ DeviceFactors: " + ele.getKey();
        			max_manyfactor_totalUser_mailContent += "<br>&emsp;&emsp;+ Total users: " + size;
        			max_manyfactor_totalUser_mailContent += "<br>&emsp;&emsp;+ UserID List: ";
        			String userIDList = arrValue.toString();
        			userIDList = userIDList.substring(1, userIDList.length() - 1);
        			max_manyfactor_totalUser_mailContent += userIDList;
        		}
        	}
        	
        	data.put("max_manyfactor_totalUser", maxTotalUsers);
        });
		
		// Field: min_gap_reqDate_userip
        queue.add(() -> {
        	List<UserIP> result = dataManager.getUserIP(timeTracking);
        	HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
                
        	for (UserIP item : result) {
                String time = item.getTime().split("\\.")[0];
                time = time.replaceAll("Z","");
                addToList(groupMap, item.getUserIP(), time + "," + item.getCampaignID(), true);
            }
        	
        	long minGapReqDate = -1;
        	for (Entry<String, ArrayList<String>> ele : groupMap.entrySet()) {
        		ArrayList<String> arrValue = ele.getValue();
        		int arrSize = arrValue.size();
        		for (int i=0; i<arrSize-1; ++i) {
        			String[] splitRes_i = arrValue.get(i).split(",");
                    String time_i = splitRes_i[0];
					String campaignID_i = splitRes_i[1];

					long firstValue = Instant.parse(time_i+"Z").getEpochSecond();

                	for (int j=i+1; j<arrSize; ++j) {
                		String[] splitRes_j = arrValue.get(j).split(",");
                        String time_j = splitRes_j[0];
                        String campaignID_j = splitRes_j[1];

						long secondValue = Instant.parse(time_j+"Z").getEpochSecond();
						long minusResult = Math.abs(firstValue - secondValue);
                		if (minGapReqDate == -1 || minGapReqDate > minusResult) {
                			minGapReqDate = minusResult;

                			min_gap_reqDate_userip_mailContent = "<br>&emsp;&emsp;+ UserIP: " + ele.getKey();
                			min_gap_reqDate_userip_mailContent += "<br>&emsp;&emsp;+ Time (first - second): " + time_i + " - " + time_j;
                			min_gap_reqDate_userip_mailContent += "<br>&emsp;&emsp;+ CampaignID (first - second): " + campaignID_i + " - " + campaignID_j;
                		}
                	}
                }
        	}
        	
        	if (minGapReqDate == -1) minGapReqDate = 0;
        	min_gap_reqDate_userip_mailContent += "<br>&emsp;&emsp;+ Min gap of reqDate: " + minGapReqDate;
        	data.put("min_gap_reqDate_userip", minGapReqDate);
        });
		
		// Field: total_VN_mno
        queue.add(() -> {
        	List<String> ValidMNOList = Arrays.asList(metricsConfig.getUserMNOValidData().split(","));
        	List<UserMNO> result = dataManager.getUserMNO(timeTracking);
        	HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
        	
        	for (UserMNO item : result) {
                addToList(groupMap, item.getCampaignID(), item.getMno() + "," + item.getUserID(), false);
            }
        	
        	int count = 0;
        	total_VN_mno_mailContent = "";
        	for(Entry<String, ArrayList<String>> ele : groupMap.entrySet()) {
                String key = ele.getKey();
                ArrayList<String> arrValue = ele.getValue();
                
                int tempCount = 0;
                for (String item : arrValue){
                    String[] arrRes = item.split(",");
                    String mno = arrRes[0];
                    String userID = arrRes[1];
                    if (!isValidMNO(ValidMNOList, mno))
                    {
                    	tempCount += 1;
                    	if (tempCount == 1) {
                    		total_VN_mno_mailContent += "<br>&emsp;&emsp;+ CampaignID: " + key;
                    	}
                    	total_VN_mno_mailContent += "<br>&emsp;&emsp;&emsp;&emsp;> " + mno + " - " + userID;
                    }
                }
                
                count += tempCount;
            }
        	
        	total_VN_mno_mailContent += "<br>&emsp;&emsp;+ Total unsuitable MNO: " + count;
        	data.put("total_VN_mno", count);
        });
		
		// Running threadpool
        ConcurrentProc threadPool = new ConcurrentProc();
        boolean runningStatus = threadPool.runningThreadPool(queue, queue.size());
        if (!runningStatus) {
        	listTimeBefores.clear();
            listTimeTracking.clear();
            logger.error("Get all data for model fail");
            return;
        };
        
        // [Continue] Process field: max_transaction_perc_increase
        double percentResult = computePercentageOfTransIncrease();
        data.put("max_transaction_perc_increase", percentResult);
        listTimeBefores.clear();
        listTimeTracking.clear();
		
        // Process model
		List<? extends InputField> inputFields = evaluator.getInputFields();
		Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
		for(InputField inputField : inputFields) {
			String fieldNameString = inputField.getName().getValue();
			if (data.containsKey(fieldNameString)) {
				FieldName inputName = inputField.getName();
				Object rawValue = data.get(fieldNameString);
				FieldValue inputValue = inputField.prepare(rawValue);
				arguments.put(inputName, inputValue);
			}
			else {
				logger.error("Model has wrong inputField");
				return;
			}
		}
		Map<FieldName, ?> results = evaluator.evaluate(arguments);
		Map<String, ?> resultRecord = EvaluatorUtil.decodeAll(results);
		String isAlert = resultRecord.get("alert").toString(); 
		
		// Alert
		if (isAlert.equals("1")) {
			String title = metricsConfig.getMetricModelTitle();
			String str_timebefore = timeBefore01 + ", " + timeBefore02 + ", " + timeBefore03 + ", " + timeBefore04; 
            String description = String.format(
            						metricsConfig.getMetricModelDescription(),
        							timeTracking, 
        							str_timebefore, 
        							max_transaction_perc_increase_mailContent, 
        							max_location_totalUser_mailContent,
        							total_location_outsidevn_mailContent, 
        							max_manyfactor_totalUser_mailContent, 
        							min_gap_reqDate_userip_mailContent,
    								total_VN_mno_mailContent);
            logger.info("Metric model information is added to alert content");
            alert.addAlertContent(title, description, "default");
		}
		logger.info("Run ok model");
	}
}
