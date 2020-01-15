package rms.alert.data;

import org.apache.kafka.common.internals.Topic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rms.alert.data.classes.*;
import rms.alert.data.configs.InfluxDBConfig;
import rms.alert.data.configs.QueriesConfig;
import rms.alert.data.configs.TopicConfig;
import rms.alert.data.repository.InfluxRepo;
import rms.alert.data.repository.RedisRepo;
import rms.alert.metrics.metrics.configs.MetricsConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import rms.alert.kafka.Producer.Sender;

@Service
public class DataManager implements Runnable {

	private final Logger logger = LogManager.getLogger(DataManager.class);

	@Autowired
	private InfluxRepo influxRepo;

	@Autowired
	private RedisRepo redisRepo;

	@Autowired
	private InfluxDBConfig influxDBConfig;

	@Autowired
	private QueriesConfig queriesConfig;

	@Autowired
	private MetricsConfig metricsConfig;

	@Autowired
	private TopicConfig topicConfig;

	public boolean saveData(String reqDate, JSONArray ja) {
		return influxRepo.saveData(reqDate, ja);
	}

	public List<CampaignAmount> getCampaignAmount(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentCampaignAmountQuery(), influxDBConfig.getMeasurementName(),
				timeTracking,topicConfig.getTopics1());
		List<CampaignAmount> result = influxRepo.queryDatabase(query, CampaignAmount.class);
		return result;
	}

	public List<CampaignAmount> getCampaignAmount(String timeTracking, String timeBefore) {
		List<CampaignAmount> result = new ArrayList<>();
		if (!timeBefore.equals("0")){
			String query = String.format(queriesConfig.getBeforeCampaignAmountQuery(), influxDBConfig.getMeasurementName(),
					timeTracking, timeBefore, timeBefore,topicConfig.getTopics1());
			result = influxRepo.queryDatabase(query, CampaignAmount.class);
		}
		return result;
	}

	public List<DeviceFactors> getDeviceFactors(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentDeviceFactorsQuery(), influxDBConfig.getMeasurementName(),
				timeTracking,topicConfig.getTopics1());
		List<DeviceFactors> result = influxRepo.queryDatabase(query, DeviceFactors.class);
		return result;
	}

	public List<TransOfAllCampaigns> getTransOfAllCampaigns(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentTransOfAllCampaignsQuery(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics1());
		List<TransOfAllCampaigns> result = influxRepo.queryDatabase(query, TransOfAllCampaigns.class);
		return result;
	}

	public List<TransOfAllCampaigns> getTransOfAllCampaigns(String timeTracking, String timeBefore) {
		String query = String.format(queriesConfig.getBeforeTransOfAllCampaignsQuery(),
				influxDBConfig.getMeasurementName(), timeTracking, timeBefore, timeBefore,topicConfig.getTopics1());
		List<TransOfAllCampaigns> result = influxRepo.queryDatabase(query, TransOfAllCampaigns.class);
		return result;
	}

	public List<UserInVietNam> getUserInVietNam(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentUserInVietNamQuery(), influxDBConfig.getMeasurementName(),
				timeTracking,topicConfig.getTopics1());
		List<UserInVietNam> result = influxRepo.queryDatabase(query, UserInVietNam.class);
		return result;
	}

	public List<UserLocation> getNumUserOnEachLocation(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentUserLocationQuery(), influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics1());
		List<UserLocation> result = influxRepo.queryDatabase(query, UserLocation.class);
		return result;
	}

	public List<UserMNO> getUserMNO(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentUserMNOQuery(), influxDBConfig.getMeasurementName(),
				timeTracking,topicConfig.getTopics1());
		List<UserMNO> result = influxRepo.queryDatabase(query, UserMNO.class);
		return result;
	}

	public List<UserIP> getUserIP(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentUserIPQuery(),influxDBConfig.getMeasurementName(),
				timeTracking, topicConfig.getTopics1());
		List<UserIP> result = influxRepo.queryDatabase(query, UserIP.class);
		return result;
	}

	public List<UserID> getUserIDsForDeviceFactors(String timeTracking, String deviceFactors) {
		String query = String.format(queriesConfig.getCurrentDeviceFactorsQueryUserIDs(),
				influxDBConfig.getMeasurementName(), timeTracking, deviceFactors,topicConfig.getTopics1());
		List<UserID> result = influxRepo.queryDatabase(query, UserID.class);
		return result;
	}

	public List<UserID> getUserIDsForUserLocation(String timeTracking, String latitude, String longitude) {
		String query = String.format(queriesConfig.getCurrentUserLocationQueryUserIDs(),
				influxDBConfig.getMeasurementName(), timeTracking, latitude, longitude,topicConfig.getTopics1());
		List<UserID> result = influxRepo.queryDatabase(query, UserID.class);
		return result;
	}

    public List<CardMapping> getCardMapping(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentCardMapping(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics2());
		List<CardMapping> result = influxRepo.queryDatabase(query, CardMapping.class);
		return result;
    }

	public List<CardMapping> getCardMapping(String timeTracking, String timeBefore) {
		String query = String.format(queriesConfig.getBeforeCardMapping(),
				influxDBConfig.getMeasurementName(), timeTracking, timeBefore, timeBefore,topicConfig.getTopics2());
		List<CardMapping> result = influxRepo.queryDatabase(query, CardMapping.class);
		return result;
	}

	public List<AccountOnboard> getAccountOnboard(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentAccountOnboard(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics3());
		List<AccountOnboard> result = influxRepo.queryDatabase(query, AccountOnboard.class);
		return result;
	}

	public List<AccountOnboard> getAccountOnboard(String timeTracking, String timeBefore) {
		String query = String.format(queriesConfig.getBeforeAccountOnboard(),
				influxDBConfig.getMeasurementName(), timeTracking, timeBefore, timeBefore,topicConfig.getTopics3());
		List<AccountOnboard> result = influxRepo.queryDatabase(query, AccountOnboard.class);
		return result;
	}

	public List<SenderReceiverLixiOpen> getSenderReceiverFromLixiOpen(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentTetLixiOpen(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics4());
		List<SenderReceiverLixiOpen> result = influxRepo.queryDatabase(query, SenderReceiverLixiOpen.class);
		return result;
	}

	public List<SenderReceiverTranslog> getSenderReceiverFromTranslog(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentTetTranslog(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics1(), influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics1());
		List<SenderReceiverTranslog> result = influxRepo.queryDatabase(query, SenderReceiverTranslog.class);
		return result;
	}

	public List<ShareLixi> getShareLixiData(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentShareLixi(),
				influxDBConfig.getMeasurementName(), timeTracking,topicConfig.getTopics5());
		List<ShareLixi> result = influxRepo.queryDatabase(query, ShareLixi.class);
		return result;
	}

	public List<UserID> getUserIDNotA30(String timeTracking) {
		String query = String.format(queriesConfig.getCurrentZpiZaloIsNotA30(),
				influxDBConfig.getMeasurementName(), timeTracking, topicConfig.getTopics6());
		List<UserID> result = influxRepo.queryDatabase(query, UserID.class);
		return result;
	}

	@Override
	public void run() {
		if (!metricsConfig.getAutoUpdateMetricDataStatus().equals("on")) return;
		logger.info("Auto update Redis");

		String timeTracking = metricsConfig.getGetAutoUpdateMetricDataTimeTracking();
		updateNumUserOnEachLocation(timeTracking);

		List<SenderReceiverLixiOpen> lixiOpens = getSenderReceiverFromLixiOpen(timeTracking);
		List<SenderReceiverTranslog> lixiTranslogs = getSenderReceiverFromTranslog(timeTracking);
		updateSame2ndReceiver(lixiOpens, lixiTranslogs);
		updateAmountPerUser(lixiOpens);
		updateNumGroupsPerUser(lixiOpens);
		logger.info("Redis updated");
	}

	private void updateNumGroupsPerUser(List<SenderReceiverLixiOpen> lixiOpens) {

		HashMap<String,ArrayList<String>> hmGroups = new HashMap<>();
		for (SenderReceiverLixiOpen item: lixiOpens){
			addToList(hmGroups,item.getReceiver(),item.getGroupZID(),false);
		}

		HashMap<String,ArrayList<String>> hmFirstMemGroups = new HashMap<>();
		hmGroups.forEach((user,listGroup)->{
			hmFirstMemGroups.put(user,getFirstMemGroups(user,listGroup,lixiOpens));
		});

		HashMap oldMap = (HashMap) redisRepo.getAllNumGroupsPerUser();
		hmFirstMemGroups.forEach((user,firstMemGroups)->{
			if (firstMemGroups.size() > 0){
				ArrayList<String> first = firstMemGroups;
				ArrayList<String> all = hmGroups.get(user);
				if (oldMap.containsKey(user)){
					String[] oldItems = oldMap.get(user).toString().replaceAll("\\s","").split(":");

					combineNewStringAndOldString(first, oldItems[0]);

					combineNewStringAndOldString(all, oldItems[1]);

				}
				redisRepo.writeNumGroupsPerUser(user,first,all);
			}
		});
	}

	private ArrayList<String> getFirstMemGroups(String user, ArrayList<String> listGroup, List<SenderReceiverLixiOpen> lixiOpens) {
		ArrayList<String> res = new ArrayList<>();
		for (String group:listGroup){
			if (checkFirstMemOfGroup(user,group,lixiOpens)){
				res.add(group);
			}
		}
		return res;
	}

	private boolean checkFirstMemOfGroup(String user, String group, List<SenderReceiverLixiOpen> lixiOpens) {
		List<SenderReceiverLixiOpen> groupData = new ArrayList<>();
		for(SenderReceiverLixiOpen item : lixiOpens){
			if (item.getGroupZID().equals(group)){
				groupData.add(item);
			}
		}
		SenderReceiverLixiOpen firstMem = getEarliestMemberInAGroup(groupData);
		SenderReceiverLixiOpen userData = getUserDataInAGroup(groupData,user);

		Long minTime = getEpochSecond(firstMem.getTime());
		Long userTime = getEpochSecond(userData.getTime());
		return userTime - minTime < 5;
	}

	private SenderReceiverLixiOpen getUserDataInAGroup(List<SenderReceiverLixiOpen> groupData, String user) {
		List<SenderReceiverLixiOpen> userData =new ArrayList<>();
		for (SenderReceiverLixiOpen item:groupData){
			if (item.getReceiver().equals(user)){
				userData.add(item);
			}
		}
		SenderReceiverLixiOpen res = getEarliestMemberInAGroup(userData);
		return res;
	}

	private SenderReceiverLixiOpen getEarliestMemberInAGroup(List<SenderReceiverLixiOpen> groupData) {
		SenderReceiverLixiOpen min = groupData.get(0);
		for (SenderReceiverLixiOpen item : groupData){
			Long minTime = getEpochSecond(min.getTime());
			Long itemTime = getEpochSecond(item.getTime());
		 	if ( minTime > itemTime){
		 		min = item;
			}
		 }
		return min;
	}

	private Long getEpochSecond(String dateTime){
		Instant instant = Instant.parse(dateTime) ;
		return instant.getEpochSecond();
	}

	private void updateAmountPerUser(List<SenderReceiverLixiOpen> lixiOpens) {
		HashMap<String,String> totalAmount = new HashMap<String,String>();
		for (SenderReceiverLixiOpen item:lixiOpens){
			addAmountToTotalAmount(totalAmount,item.getReceiver(),item.getAmount());
		}

		HashMap<String,ArrayList<String>> totalPackages = new HashMap<String,ArrayList<String>>();
		for (SenderReceiverLixiOpen item: lixiOpens){
			addToList(totalPackages,item.getReceiver(),item.getGroupID(),false);
		}

		HashMap<String,ArrayList<String>> totalGroup = new HashMap<String,ArrayList<String>>();
		for (SenderReceiverLixiOpen item: lixiOpens){
			addToList(totalGroup,item.getReceiver(),item.getGroupZID(),false);
		}

		HashMap oldMap = (HashMap) redisRepo.getAllAmountPerUser();
		totalAmount.forEach((user,amount)->{
			ArrayList<String> groupIDs = totalPackages.get(user);
			ArrayList<String> groupZIDs = totalGroup.get(user);
			if (oldMap.containsKey(user)){

				String[] oldItems = oldMap.get(user).toString().replaceAll("\\s","").split(":");

				amount = addNewAmountToOldAmount(amount, oldItems[0]);

				combineNewStringAndOldString(groupIDs, oldItems[1]);

				combineNewStringAndOldString(groupZIDs, oldItems[2]);

			}
			redisRepo.writeAmountPerUser(user,amount,groupIDs,groupZIDs);
		});
	}

	private void updateSame2ndReceiver(List<SenderReceiverLixiOpen> lixiOpens,List<SenderReceiverTranslog> lixiTranslogs) {
		HashMap oldMap = (HashMap) redisRepo.getAllListSenderOfReceiver();

		HashMap<String,ArrayList<String>> currentMap = new HashMap<String,ArrayList<String>>();
		HashMap<String,String> currentMapAmount = new HashMap<String,String>();

		for (SenderReceiverTranslog lixiTranslog: lixiTranslogs){
			for (SenderReceiverLixiOpen lixiOpen:lixiOpens){
				if (lixiTranslog.getSender().equals(lixiOpen.getReceiver())) {
					addToList(currentMap, lixiTranslog.getReceiver(), lixiTranslog.getSender(), false);
					addAmountToTotalAmount(currentMapAmount,lixiTranslog.getReceiver(),lixiTranslog.getAmount());
				}
			}
		}

		currentMap.forEach((receiver,listSender)->{
			String currentAmount = currentMapAmount.get(receiver);
			if (oldMap.containsKey(receiver)){
				String[] listOldSender = oldMap.get(receiver).toString().replaceAll("\\s","").split(":");

				combineNewStringAndOldString(listSender, listOldSender[0]);
				currentAmount = addNewAmountToOldAmount(currentAmount, listOldSender[1]);
			}
			redisRepo.writeListSenderOfReceiver(receiver,listSender,currentAmount);
		});
	}

	private void combineNewStringAndOldString(ArrayList<String> listSender, String s) {
		String[] listOldUserIDSender = s.split(",");
		for (String oldSender:listOldUserIDSender){
			if (!listSender.contains(oldSender)){
				listSender.add(oldSender);
			}
		}
	}

	private String addNewAmountToOldAmount(String currentAmount, String listOldSender) {
		String oldAmount = listOldSender;
		long numOldAmount = (long) Double.parseDouble(oldAmount);
		long numCurrentAmount = (long) (Double.parseDouble(currentAmount)+numOldAmount);
		currentAmount = String.valueOf(numCurrentAmount);
		return currentAmount;
	}

	private void updateNumUserOnEachLocation(String timeTracking){
		HashMap oldMap = (HashMap) redisRepo.getAllLocationWithNumUserCurrent();

		List<UserLocation> users = getNumUserOnEachLocation(timeTracking);

		HashMap<String,ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
		for (UserLocation item:users){
			addToList(hashMap,item.getLatitude()+"-"+item.getLongitude(),item.getUserID(),false);
		}

		hashMap.forEach((k,v)->
		{
			if (oldMap.containsKey(k)){
				int total = v.size()+Integer.parseInt(oldMap.get(k).toString());
				redisRepo.writeNumUserAtLocation(k,total);
			}else
				redisRepo.writeNumUserAtLocation(k,v.size());
		});
	}

	private void addAmountToTotalAmount(HashMap<String, String> currentMapAmount, String receiver, String amount) {
		String totalAmount = currentMapAmount.get(receiver);
		if (totalAmount == null){
			long res = (long) Double.parseDouble(amount);
			totalAmount = String.valueOf(res);
		}else{
			double numAmount = Double.parseDouble(totalAmount);
			numAmount+=Double.parseDouble(amount);
			long res = (long) numAmount;
			totalAmount = String.valueOf(res);
		}
		currentMapAmount.put(receiver,totalAmount);
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
}
