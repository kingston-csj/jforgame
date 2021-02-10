package com.kingston.jforgame.server.thread;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class CommonBusinessExecutor {

    private Logger logger = LoggerFactory.getLogger(CommonBusinessExecutor.class);

    /**
     * task worker pool
     */
    private final List<TaskWorker> workerPool = new ArrayList<>();

    private final AtomicBoolean run = new AtomicBoolean(true);

    /**
     * 任务队列总线
     */
    public LinkedBlockingQueue<Runnable> ROOT_QUEUE = new LinkedBlockingQueue<>();

    public CommonBusinessExecutor(String name, int size) {
        for (int i = 1; i <= size; i++) {
            TaskWorker worker = new TaskWorker();
            workerPool.add(worker);
            new NamedThreadFactory(name + "-" + i).newThread(worker).start();
        }
    }

    private class TaskWorker implements Runnable {

        @Override
        public void run() {
            //accept task all the time
            while (run.get()) {
                try {
                    Runnable task = ROOT_QUEUE.take();
                    task.run();
                    Thread t = Thread.currentThread();
                } catch (Exception e) {
                    logger.error("task execute failed ", e);
                }
            }
        }
    }
}


