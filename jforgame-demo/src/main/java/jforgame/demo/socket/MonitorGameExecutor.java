package jforgame.demo.socket;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.socket.share.DispatchThreadModel;
import jforgame.socket.share.task.BaseGameTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonitorGameExecutor extends DispatchThreadModel {

    private ConcurrentMap<Thread, BaseGameTask> currentTasks = new ConcurrentHashMap<>();

    private final long MONITOR_INTERVAL = 5000L;

    private final long MAX_EXEC_TIME = 30000L;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public MonitorGameExecutor() {
        new NamedThreadFactory("message-business-monitor").newThread(new TaskMonitor()).start();
    }

    @Override
    public void accept(BaseGameTask task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        super.accept(new BaseGameTask() {
            @Override
            public void action() {
                Thread t = Thread.currentThread();
                currentTasks.put(t, task);
                task.action();
                currentTasks.remove(t);
            }
        });
    }

    @Override
    public void shutDown() {
        running.compareAndSet(true, false);
        super.shutDown();
    }

    class TaskMonitor implements Runnable {

        @Override
        public void run() {
            while (running.get()){
                try {
                    Thread.sleep(MONITOR_INTERVAL);
                } catch (InterruptedException e) {
                }

                for (Map.Entry<Thread, BaseGameTask> entry : currentTasks.entrySet()) {
                    Thread t = entry.getKey();
                    BaseGameTask task = entry.getValue();
                    if (task != null) {
                        long now = System.currentTimeMillis();
                        if (now - task.getStartTime() > MAX_EXEC_TIME) {
                            LoggerUtils.error("[{}]执行任务超时", task.getName());
                        }
                    }
                }
            }
        }
    }

}
