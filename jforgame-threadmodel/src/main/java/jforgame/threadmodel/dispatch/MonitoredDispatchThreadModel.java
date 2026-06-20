package jforgame.threadmodel.dispatch;


import jforgame.commons.thread.NamedThreadFactory;
import jforgame.commons.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Enhanced version of {@link DispatchThreadModel} with thread monitoring capability.
 * When a business thread is blocked by deadlock, infinite loop, or long execution time,
 * the monitoring thread will detect it and automatically print the stack trace information of the corresponding thread.
 *
 * @since 3.3.0
 */
public class MonitoredDispatchThreadModel extends DispatchThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(MonitoredDispatchThreadModel.class);

    private final ConcurrentMap<Thread, BaseDispatchTask> currentTasks = new ConcurrentHashMap<>();

    /**
     * Default monitor interval
     */
    private static final long DEFAULT_MONITOR_INTERVAL = TimeUtil.MILLIS_PER_SECOND * 2;
    /**
     * Default maximum execution time
     */
    private static final long DEFAULT_MAX_EXEC_TIME = TimeUtil.MILLIS_PER_SECOND;

    /**
     * Monitor thread tick interval
     */
    private final long monitorInterval;

    /**
     * Maximum task execution time, tasks exceeding this time will be marked as timeout
     */
    private final long maxExecMillis;

    public MonitoredDispatchThreadModel() {
        this(Runtime.getRuntime().availableProcessors(), DEFAULT_MONITOR_INTERVAL, DEFAULT_MAX_EXEC_TIME);
    }

    public MonitoredDispatchThreadModel(int threadSize, long monitorInterval, long maxExecMillis) {
        super(threadSize);
        new NamedThreadFactory("message-business-monitor").newThread(new TaskMonitor()).start();
        this.monitorInterval = monitorInterval;
        this.maxExecMillis = maxExecMillis;
    }

    @Override
    public void accept(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        BaseDispatchTask dispatchTask = (BaseDispatchTask) task;
        // Wrapper task
        BaseDispatchTask wrapper = new BaseDispatchTask() {
            @Override
            public void action() {
                Thread t = Thread.currentThread();
                currentTasks.put(t, dispatchTask);
                try {
                    task.run();
                } finally {
                    // Prevent abnormal execution from continuously showing timeout
                    currentTasks.remove(t);
                }
            }
        };
        wrapper.setDispatchKey(dispatchTask.getDispatchKey());
        super.accept(wrapper);
    }

    /**
     * Task monitor thread
     */
    class TaskMonitor implements Runnable {

        @Override
        public void run() {
            while (!isShutdown()) {
                try {
                    Thread.sleep(monitorInterval);
                } catch (InterruptedException ignored) {
                }

                for (Map.Entry<Thread, BaseDispatchTask> entry : currentTasks.entrySet()) {
                    Thread t = entry.getKey();
                    BaseDispatchTask task = entry.getValue();
                    if (task != null) {
                        long now = System.currentTimeMillis();
                        if (now - task.getStartTime() > maxExecMillis) {
                            logger.error("Detected thread [{}] task timeout", t.getName());
                            // Print thread stack trace
                            logger.error("Stack trace for thread [{}]: {}", t.getName(), formatStackTrace(t.getStackTrace()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Format thread stack trace
     */
    private String formatStackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace == null || stackTrace.length == 0) {
            return "No stack trace information";
        }

        return "\n" + Arrays.stream(stackTrace).map(element -> String.format("  %s.%s(%s:%d)", element.getClassName(),    // Class name
                        element.getMethodName(),  // Method name
                        element.getFileName(),    // File name
                        element.getLineNumber())) // Line number
                .collect(Collectors.joining("\n"));
    }

}

