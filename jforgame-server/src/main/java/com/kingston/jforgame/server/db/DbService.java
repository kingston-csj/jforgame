package com.kingston.jforgame.server.db;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kingston.jforgame.common.utils.BlockingUniqueQueue;
import com.kingston.jforgame.common.utils.NamedThreadFactory;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 用户数据异步持久化的服务
 * @author kingston
 */
public class DbService {

	private static volatile DbService instance;

	public static DbService getInstance() {
		if (instance ==  null) {
			synchronized (DbService.class) {
				if (instance ==  null) {
					instance = new DbService();
				}
			}
		}
		return instance;
	}

	/**
	 * start consumer thread
	 */
	public void init() {
		new NamedThreadFactory("db-save-service").newThread(new Worker()).start();
	}

	private BlockingQueue<BaseEntity> queue = new BlockingUniqueQueue<>();

	private final AtomicBoolean run = new AtomicBoolean(true);

	public void add2Queue(BaseEntity entity) {
		this.queue.add(entity);
	}


	private class Worker implements Runnable {
		@Override
		public void run() {
			while(run.get()) {
				try {
					BaseEntity entity = queue.take();
					saveToDb(entity);
				} catch (InterruptedException e) {
					LoggerUtils.error("", e);
				}
			}
		}
	}

	/**
	 * 数据真正持久化
	 * @param entity
	 */
	private void saveToDb(BaseEntity entity) {
		entity.doBeforeSave();
		String saveSql = entity.getSaveSql();
		if (DbUtils.executeSql(saveSql)) {
			entity.resetDbStatus();
		}
	}
	
}
