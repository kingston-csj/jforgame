package jforgame.server.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerManager {

    /**
     * @param command
     * @param initialDelay 毫秒数
     * @param period       毫秒数
     * @return
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay, long period) {
        ScheduledExecutorService service = ThreadCenter.getScheduledExecutorService();
        return service.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 注册timeout任务
     *
     * @param task
     * @param delay
     */
    public static ScheduledFuture schedule(Runnable task, long delay) {
        ScheduledExecutorService service = ThreadCenter.getScheduledExecutorService();
        ScheduledFuture taskFuture = service.schedule(task, delay, TimeUnit.MILLISECONDS);
        return taskFuture;
    }
}
