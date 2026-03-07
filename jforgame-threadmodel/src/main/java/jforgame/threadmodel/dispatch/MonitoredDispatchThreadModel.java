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
 * 对{@link DispatchThreadModel}进行增强，使之拥有监控线程的能力
 * 当业务线程因为死锁，死循环，耗时过长等情况时，会被监控线程检测，并自动打印对应线程的堆栈信息|
 * @since 3.3.0
 */
public class MonitoredDispatchThreadModel extends DispatchThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(MonitoredDispatchThreadModel.class);

    private final ConcurrentMap<Thread, BaseDispatchTask> currentTasks = new ConcurrentHashMap<>();

    /**
     * 默认监控间隔
     */
    private static final long DEFAULT_MONITOR_INTERVAL = TimeUtil.MILLIS_PER_SECOND * 2;
    /**
     * 默认最大执行时间
     */
    private static final long DEFAULT_MAX_EXEC_TIME = TimeUtil.MILLIS_PER_SECOND;

    /**
     * 监控线程每隔XX时间tick一次
     */
    private final long monitorInterval;

    /**
     * 任务最大执行时间，超过该时间，会被判定为超时
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
        // 代理任务
        BaseDispatchTask wrapper = new BaseDispatchTask() {
            @Override
            public void action() {
                Thread t = Thread.currentThread();
                currentTasks.put(t, dispatchTask);
                try {
                    task.run();
                } finally {
                    // 防止执行异常，一直显示超时
                    currentTasks.remove(t);
                }
            }
        };
        wrapper.setDispatchKey(dispatchTask.getDispatchKey());
        super.accept(wrapper);
    }

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
                            logger.error("监测到线程[{}]执行任务超时", t.getName());
                            // 打印线程栈信息
                            logger.error("线程[{}]对应的堆栈信息:{}", t.getName(), formatStackTrace(t.getStackTrace()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 格式化线程栈信息
     */
    private String formatStackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace == null || stackTrace.length == 0) {
            return "无堆栈信息";
        }

        return "\n" + Arrays.stream(stackTrace).map(element -> String.format("  %s.%s(%s:%d)", element.getClassName(),    // 类名
                        element.getMethodName(),  // 方法名
                        element.getFileName(),    // 文件名
                        element.getLineNumber())) // 行号
                .collect(Collectors.joining("\n"));
    }

}

