package jforgame.socket.share;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.socket.share.task.BaseGameTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于线程组的线程模型，该模型会预定义一组线程，每个线程会绑定一个任务队列。当接收一个新任务的时候，会根据{@link BaseGameTask#getDispatchKey()}绑定到一个具体的线程，
 * 该线程会从自己的任务队列中取出任务并执行。
 */
public class DispatchThreadModel implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(DispatchThreadModel.class);

    /**
     * task worker pool
     */
    private final Worker[] workerPool;

    private static final AtomicBoolean running = new AtomicBoolean(true);


    public DispatchThreadModel() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * @param workCapacity worker count in thread group
     */
    public DispatchThreadModel(int workCapacity) {
        ThreadFactory threadFactory = new NamedThreadFactory("message-business");
        workerPool = new Worker[workCapacity];
        for (int i = 0; i < workCapacity; i++) {
            Worker w = new Worker();
            workerPool[i] = w;
            threadFactory.newThread(w).start();
        }
    }

    private static class Worker implements Runnable {
        LinkedBlockingQueue<BaseGameTask> taskQueue = new LinkedBlockingQueue<>();

        void receive(BaseGameTask task) {
            taskQueue.add(task);
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    BaseGameTask task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    // TODO other way?
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * when receiving a task, the executor will calculate the thread index based on the {@link BaseGameTask#getDispatchKey()}
     * for example, if the executor group has N threads, the task will be dispatched to the thread which index is (dispatchKey() % N)
     *
     * @param task command task
     */
    @Override
    public void accept(BaseGameTask task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        if (!running.get()) {
            return;
        }
        int distributeKey = (int) (task.getDispatchKey() % workerPool.length);
        workerPool[distributeKey].receive(task);
    }

    /**
     * when this executor shuts down, it will no longer accept new task
     * and the remained tasks will be abandoned either.
     */
    @Override
    public void shutDown() {
        running.compareAndSet(true, false);
    }

}
