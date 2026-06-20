package jforgame.threadmodel.dispatch;


import jforgame.commons.thread.NamedThreadFactory;
import jforgame.threadmodel.ThreadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Keyword-based dispatch thread model.
 * This model predefines a group of threads, each thread bound to a task queue.
 * When receiving a new task, it binds to a specific thread based on {@link BaseDispatchTask#getDispatchKey()}.
 * The thread continuously takes tasks from its own queue and executes them until the queue is empty, then blocks.
 */
public class DispatchThreadModel implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(DispatchThreadModel.class);

    /**
     * Task dispatcher
     */
    private final Worker[] workerPool;

    private static final AtomicBoolean running = new AtomicBoolean(true);


    public DispatchThreadModel() {
        this(Runtime.getRuntime().availableProcessors() + 2);
    }

    /**
     * Worker thread count
     *
     * @param workCapacity worker thread count
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
        LinkedBlockingQueue<BaseDispatchTask> taskQueue = new LinkedBlockingQueue<>();

        void receive(BaseDispatchTask task) {
            taskQueue.add(task);
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    BaseDispatchTask task = taskQueue.take();
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
     * When receiving a task, calculates the thread index for dispatch based on {@link BaseDispatchTask#getDispatchKey()}.
     * For example: if the thread pool has N threads, the task will be dispatched to the thread with index (dispatchKey % N)
     *
     * @param task task to execute
     */
    @Override
    public void accept(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        if (!running.get()) {
            return;
        }
        BaseDispatchTask dispatchTask = (BaseDispatchTask) task;
        int distributeKey = (int) (dispatchTask.getDispatchKey() % workerPool.length);
        workerPool[distributeKey].receive(dispatchTask);
    }

    /**
     * Shuts down the executor, no longer accepts new tasks, and remaining tasks will be discarded.
     */
    @Override
    public void shutDown() {
        running.compareAndSet(true, false);
    }

    @Override
    public boolean isShutdown() {
        return !running.get();
    }


}