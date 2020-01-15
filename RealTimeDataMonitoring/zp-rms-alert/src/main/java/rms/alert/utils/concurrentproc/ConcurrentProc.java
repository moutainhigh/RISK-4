package rms.alert.utils.concurrentproc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

//Ref: https://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
public class ConcurrentProc {

    private final Logger logger = LogManager.getLogger(ConcurrentProc.class);
    public static final int GET_TIME_OUT = 10;

    public boolean runningThreadPool(BlockingQueue<Runnable> queue, int numOfThread) {
        try {
            logger.info("Threadpool is running");

            ExecutorService ES = Executors.newFixedThreadPool(numOfThread);

            for (int i = 0; i < numOfThread; ++i) {
                ES.execute(() -> {
                    try {
                        queue.take().run();
                    } catch (InterruptedException e) {
                        logger.error("Run task of queue - {}", e.getMessage());
                    }
                });
            }
            ES.shutdown();
            try {
                if (ES.awaitTermination(GET_TIME_OUT, TimeUnit.MINUTES)) {
                   logger.info("Threadpool task completed");
                } else {
                    logger.info("Threadpool shutdown by timeout");
                    ES.shutdownNow();
                    return false;
                }
            } catch (InterruptedException e) {
                logger.error("Terminate threadpool - {}", e.getMessage());
            }
        } catch (IllegalArgumentException | NullPointerException | RejectedExecutionException e) {
            logger.error("Execute threadpool - {}", e.getMessage());
        }
        return true;
    }
}
