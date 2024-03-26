package jforgame.socket.share;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.socket.share.task.BaseGameTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides a mixed thread executor group
 * when accepting a task, the thread executor will dispatch it into
 * a binding thread according to {@link BaseGameTask#getDispatchKey()}
 *
 */
public class DispatchThreadModel implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(DispatchThreadModel.class);

    private final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
    /**
     * task worker pool
     */
    private final Worker[] workerPool = new Worker[CORE_SIZE];

    private static final AtomicBoolean running = new AtomicBoolean(true);


    public DispatchThreadModel() {
        ThreadFactory threadFactory = new NamedThreadFactory("message-business");
        for (int i = 0; i < CORE_SIZE; i++) {
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
        int distributeKey = (int) (task.getDispatchKey() % CORE_SIZE);
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
