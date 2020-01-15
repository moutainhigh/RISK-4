package rms.alert.utils.interval;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetIntervalTimerTask {

	private static final Logger logger = LogManager.getLogger();
	private Timer timer;

	public SetIntervalTimerTask() {
		super();
		this.timer = new Timer();
	}

	public void startInterval(String waitTime, TimerTask task) {
		try {
			timer.scheduleAtFixedRate(task, getTimePrecision(waitTime), getTimePrecision(waitTime));
			logger.info("Started new schedule {}", task.toString());
		} catch (Exception e) {
			logger.error("Started new schedule {} fail", task.toString());
		}
	}

	public void stopIntervalOfAllTasks() {
		this.timer.cancel();
	}

	private long getTimePrecision(String value) {
		long l = 0;
		String val = "";
		try {
			if (value.endsWith("d") || value.endsWith("D")) {
				val = value.substring(0, value.length() - 1);
				l = Long.parseLong(val) * 24 * 60 * 60 * 1000;
			} else if (value.endsWith("h") || value.endsWith("H")) {
				val = value.substring(0, value.length() - 1);
				l = Long.parseLong(val) * 60 * 60 * 1000;
			} else if (value.endsWith("m") || value.endsWith("M")) {
				val = value.substring(0, value.length() - 1);
				l = Long.parseLong(val) * 60 * 1000;
			} else if (value.endsWith("s") || value.endsWith("S")) {
				val = value.substring(0, value.length() - 1);
				l = Long.parseLong(val) * 1000;
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			logger.error("Unsuitable wait time, automatically set to 10 minutes");
			l = Long.parseLong("10") * 60 * 1000;
		}

		return l;
	}

}
