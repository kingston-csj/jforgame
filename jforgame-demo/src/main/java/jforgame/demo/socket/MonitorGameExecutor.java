package jforgame.demo.socket;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.threadmodel.dispatch.BaseDispatchTask;
import jforgame.threadmodel.dispatch.DispatchThreadModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonitorGameExecutor extends DispatchThreadModel {

    private ConcurrentMap<Thread, Runnable> currentTasks = new ConcurrentHashMap<>();

    private final long MONITOR_INTERVAL = 5000L;

    private final long MAX_EXEC_TIME = 30000L;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public MonitorGameExecutor() {
        new NamedThreadFactory("message-business-monitor").newThread(new TaskMonitor()).start();
    }

    @Override
    public void accept(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }

        BaseDispatchTask target = (BaseDispatchTask) task;

        // 代理任务
        BaseDispatchTask wrapper = new BaseDispatchTask() {
            @Override
            public void action() {
                Thread t = Thread.currentThread();
                currentTasks.put(t, task);
                try {
                    target.action();
                } finally {
                    // 防止执行异常，一直显示超时
                    currentTasks.remove(t);
                }
            }
        };
        wrapper.setDispatchKey(target.getDispatchKey());
        super.accept(wrapper);
    }

    @Override
    public void shutDown() {
        running.compareAndSet(true, false);
        super.shutDown();
    }

    class TaskMonitor implements Runnable {

        @Override
        public void run() {
            while (running.get()) {
                try {
                    Thread.sleep(MONITOR_INTERVAL);
                } catch (InterruptedException e) {
                }

                for (Map.Entry<Thread, Runnable> entry : currentTasks.entrySet()) {
                    Thread t = entry.getKey();
                    Runnable task = entry.getValue();
                    BaseDispatchTask dispatchTask = (BaseDispatchTask) task;
                    if (dispatchTask != null) {
                        long now = System.currentTimeMillis();
                        if (now - dispatchTask.getStartTime() > MAX_EXEC_TIME) {
                            LoggerUtils.error("[{}]执行任务超时", dispatchTask.getName());
                        }
                    }
                }
            }
        }
    }

}
