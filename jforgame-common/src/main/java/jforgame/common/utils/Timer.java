package jforgame.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jdk Timer工具的另一个实现
 * 区别：
 * 内部直接使用PriorityQueue作为任务工作队列
 * 运行过程中，若有一个task发生异常，整个Timer定时器不会关闭（jdk自带的timer会关闭）
 * 任务单元直接使用Runnable接口
 */
public class Timer {

    final static Logger logger = LoggerFactory.getLogger(Timer.class);

    private PriorityQueue<TimerTask> queue;

    private final TimerThread thread;

    private static final AtomicInteger nextSerialNumber = new AtomicInteger(0);

    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    public Timer() {
        this("Timer-" + serialNumber());
    }

    public Timer(boolean daemon) {
        this("Timer-" + serialNumber(), daemon);
    }

    public Timer(String threadName) {
        this.queue = new PriorityQueue<>();
        this.thread = new TimerThread(this.queue);
        this.thread.setName(threadName);
        this.thread.start();
    }

    public Timer(String threadName, boolean daemon) {
        this.queue = new PriorityQueue();
        this.thread = new TimerThread(this.queue);
        this.thread.setName(threadName);
        this.thread.setDaemon(daemon);
        this.thread.start();
    }

    public void schedule(Runnable task, long delay) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else {
            this.schedule0(task, System.currentTimeMillis() + delay, 0L);
        }
    }

    public void schedule(Runnable task, Date start) {
        this.schedule0(task, start.getTime(), 0L);
    }

    public void scheduleAtFixedRate(Runnable task, long delay, long period) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            this.schedule0(task, System.currentTimeMillis() + delay, period);
        }
    }

    public void scheduleAtFixedRate(Runnable task, Date start, long period) {
        if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            this.schedule0(task, start.getTime(), period);
        }
    }

    private void schedule0(Runnable task, long time, long period) {
        if (time < 0L) {
            throw new IllegalArgumentException("Illegal execution time.");
        } else {
            if (Math.abs(period) > 4611686018427387903L) {
                period >>= 1;
            }

            TimerTask timerTask = new TimerTask(task);
            synchronized(this.queue) {
                if (!this.thread.run) {
                    throw new IllegalStateException("Timer already cancelled.");
                } else {
                    synchronized(timerTask.lock) {
                        if (timerTask.state != 0) {
                            throw new IllegalStateException("Task already scheduled or cancelled");
                        }

                        timerTask.nextExecutionTime = time;
                        timerTask.period = period;
                        timerTask.state = TimerTask.SCHEDULED;
                    }

                    this.queue.add(timerTask);
                    if (this.queue.peek() == timerTask) {
                        this.queue.notify();
                    }
                }
            }
        }
    }

    public void cancel() {
        PriorityQueue<TimerTask> var1 = this.queue;
        synchronized(this.queue) {
            this.thread.run = false;
            this.queue.clear();
            this.queue.notify();
        }
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(() -> {
            System.out.println("short task");
        }, 1000);

        timer.schedule(() -> {
            System.out.println("long task");
        }, 5000);

        timer.scheduleAtFixedRate(() -> {
            System.out.println("frame task");
        }, 0, 1000);

    }

}

class TimerThread extends Thread {

    boolean run = true;
    private PriorityQueue<TimerTask> queue;

    public TimerThread(PriorityQueue<TimerTask> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            synchronized(queue) {
                run = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }

    /**
     * The main timer loop.  (See class comment.)
     */
    private void mainLoop() {
        while (true) {
            try {
                TimerTask task;
                boolean taskFired;
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && run) {
                        queue.wait();
                    }
                    if (queue.isEmpty()) {
                        break; // Queue is empty and will forever remain; die
                    }
                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    task = queue.peek();
                    synchronized(task.lock) {
                        if (task.state == TimerTask.CANCELLED) {
                            queue.poll();
                            continue;  // No action required, poll queue again
                        }
                        currentTime = System.currentTimeMillis();
                        executionTime = task.nextExecutionTime;
                        if (taskFired = (executionTime<=currentTime)) {
                            task = queue.poll();
                            if (task.period == 0) { // Non-repeating, remove
                                task.state = TimerTask.EXECUTED;
                            } else { // Repeating task, reschedule
                                long nextExecTime = task.period < 0 ?
                                        currentTime - task.period
                                        : executionTime + task.period;
                                task.nextExecutionTime = nextExecTime;
                                queue.add(task);
                            }
                        }
                    }
                    if (!taskFired) {// Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                    }
                }
                if (taskFired) { // Task fired; run it, holding no locks
                    try {
                        task.run();
                    }catch (Exception e) {
                        Timer.logger.error("timer任务执行异常", e);
                    }
                }
            } catch(InterruptedException e) {
                // ignore it
            }
        }
    }

}
class TimerTask implements Runnable, Comparable<TimerTask> {

    private Runnable task;

    final Object lock = new Object();

    int state = VIRGIN;

    static final int VIRGIN = 0;

    static final int SCHEDULED   = 1;

    static final int EXECUTED    = 2;

    static final int CANCELLED   = 3;

    long nextExecutionTime;

    long period = 0;

    TimerTask(Runnable task) {
        this.task = task;
    }

    @Override
    public int compareTo(TimerTask o) {
       if (o.nextExecutionTime > this.nextExecutionTime) {
           return -1;
       }
        if (o.nextExecutionTime < this.nextExecutionTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public void run() {
        task.run();
    }
}
