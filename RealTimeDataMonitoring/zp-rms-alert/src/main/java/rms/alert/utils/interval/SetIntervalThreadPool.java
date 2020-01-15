package rms.alert.utils.interval;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetIntervalThreadPool {

	private static final Logger logger = LogManager.getLogger();
	private ScheduledExecutorService executor;
	public static final int NUM_OF_THREAD = 15;

	public SetIntervalThreadPool() {
		super();
		executor = Executors.newScheduledThreadPool(NUM_OF_THREAD);
	}

	public void startInterval(String waitTime, Runnable task) {
		try {
			executor.scheduleAtFixedRate(task, getTimePrecision(waitTime), getTimePrecision(waitTime), TimeUnit.MILLISECONDS);
			logger.info("Started new schedule {}", task.toString());
		} catch (Exception e) {
			logger.error("Started new schedule {} fail", task.toString());
		}
	}

	public void stopIntervalOfAllTasks() {
		try {
			// Waits (maximum time is 10 seconds) for termination
			executor.awaitTermination(10, TimeUnit.SECONDS);
			/*
			 * Initiates an orderly shutdown in which previously submitted tasks are
			 * executed, but no new tasks will be accepted. Invocation has no additional
			 * effect if already shut down. This method does not wait for previously
			 * submitted tasks to complete execution. Use awaitTermination to do that.
			 */
			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated()) {
			}
			logger.info("Shutdown successfully");
		} catch (InterruptedException e) {
			logger.error("Shutdown fail");
		}
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
