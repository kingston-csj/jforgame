package com.kingston.jforgame.socket.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.common.thread.NamedThreadFactory;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;

/**
 * task dispatch context
 * when a message is wrapped into a task, context will add it to appointed task queue
 * according distributing strategy
 * @author kingston
 */
public enum TaskHandlerContext {

	/** 枚举单例 */
	INSTANCE;

	private static Logger logger = LoggerFactory.getLogger(TaskHandlerContext.class);
	private final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
	/** task worker pool */
	private final List<TaskWorker> workerPool = new ArrayList<>();

	private final AtomicBoolean run = new AtomicBoolean(true);

	public void initialize() {
		for (int i=1; i<=CORE_SIZE; i++) {
			TaskWorker worker = new TaskWorker(i);
			workerPool.add(worker);
			new NamedThreadFactory("message-task-handler").newThread(worker).start();
		}
	}

	/**
	 * @param task
	 */
	public void acceptTask(AbstractDistributeTask task) {
		if (task == null) {
			throw new NullPointerException("task is null");
		}
		int distributeKey = task.distributeKey() % workerPool.size();
		workerPool.get(distributeKey).addTask(task);
	}

	/**
	 * shut context
	 */
	public void shutDown() {
		run.set(false);
	}

	private class TaskWorker implements Runnable {

		/** worker id */
		private int id;
		/** task consumer queue */
		private BlockingQueue<AbstractDistributeTask> taskQueue = new LinkedBlockingQueue<>();

		TaskWorker(int index) {
			this.id = index;
		}

		public void addTask(AbstractDistributeTask task) {
			this.taskQueue.add(task);
		}

		@Override
		public void run() {
			//accept task all the time
			while(run.get()) {
				try {
					AbstractDistributeTask task = taskQueue.take();
					task.markStartMillis();
					task.action();
					task.markEndMillis();

					//if it is TimerTask and should run again, add it to queue
					if (task instanceof TimerTask) {
						TimerTask timerTask = (TimerTask)task;
						if (timerTask.canRunAgain()) {
							addTask(task);
						}
					}
				} catch (Exception e) {
					logger.error("task Worker" + id, e);
				}
			}
		}
	}
}