package com.kingston.net.context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.kingston.utils.NameableThreadFactory;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息任务处理器
 * @author kingston
 */
public enum TaskHandlerContext {

	/** 单例 */
	INSTANCE;

	private final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
	/** 工作者线程池 */
	private final List<TaskWorker> workerPool = new ArrayList<>();

	private final AtomicBoolean run = new AtomicBoolean(true);

	public void initialize() {
		for (int i=0; i<CORE_SIZE+1; i++) {
			TaskWorker worker = new TaskWorker(i);
			workerPool.add(worker);
			new NameableThreadFactory("message-task-handler").newThread(worker).start();
		}
	}

	/**
	 * 接受消息
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
	 * 关闭消息入口
	 */
	public void shutDown() {
		run.set(false);
	}

	private class TaskWorker implements Runnable {

		/** 工作者唯一号 */
		private int workerIndex;
		/** 生产者队列 */
		private BlockingQueue<AbstractDistributeTask> taskQueue = new LinkedBlockingQueue<>();

		TaskWorker(int index) {
			this.workerIndex = index;
		}

		public void addTask(AbstractDistributeTask task) {
			this.taskQueue.add(task);
		}

		@Override
		public void run() {
			//死循环读消息
			while(run.get()) {
				try {
					AbstractDistributeTask task = taskQueue.take();
					task.markStartMillis();
					task.action();
					task.markEndMillis();

					//如果是timer任务，检查是否需要重新丢入队列
					if (task instanceof TimerTask) {
						TimerTask timerTask = (TimerTask)task;
						if (timerTask.canRunAgain()) {
							addTask(task);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}