package jforgame.server.game.core;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.server.logs.LoggerUtils;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerManager {

    private static SchedulerManager instance = new SchedulerManager();

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(2, new NamedThreadFactory("common-scheduler"));


    public static SchedulerManager getInstance() {
        return instance;
    }

    /**
     * @param command
     * @param initialDelay 毫秒数
     * @param period       毫秒数
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay, long period) {
        return service.scheduleAtFixedRate(new LogTask(command), initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @param command
     * @param delay   延迟毫秒数
     * @return
     */
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay) {
        return service.schedule(new LogTask(command), delay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutDown() {
        service.shutdown();
        service.shutdownNow();
        LoggerUtils.error("定时器关闭结束");
    }


    private static class LogTask implements Runnable {

        Runnable wrapper;

        public LogTask(Runnable wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void run() {
            try {
                wrapper.run();
            } catch (Exception e) {
                LoggerUtils.error("定时任务执行异常", e);
            }
        }
    }

}