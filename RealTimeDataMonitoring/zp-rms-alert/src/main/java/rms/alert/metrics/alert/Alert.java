package rms.alert.metrics.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import rms.alert.metrics.alert.classes.SMTP;
import rms.alert.utils.interval.SetIntervalTimerTask;

import java.time.LocalDateTime;

@Service
public class Alert implements CommandLineRunner {

	private SetIntervalTimerTask siTask = new SetIntervalTimerTask();

	@Autowired
	private SMTP smtpAlert;

	@Override
	public void run(String... args) throws Exception {
//		while (true) {
//			if (LocalDateTime.now().getMinute() == 10 || LocalDateTime.now().getMinute() == 40) {
//				break;
//			}
//		}
		
		siTask.startInterval(smtpAlert.getWaitTime(), smtpAlert);
	}

	public void addAlertContent(String title, String description, String imgFileName) {
		smtpAlert.addAlertContent(title, description, imgFileName);
	}

}
