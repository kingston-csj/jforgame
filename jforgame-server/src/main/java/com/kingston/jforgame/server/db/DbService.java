package com.kingston.jforgame.server.db;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import com.kingston.jforgame.common.utils.BlockingUniqueQueue;
import com.kingston.jforgame.orm.SqlFactory;
import com.kingston.jforgame.server.game.database.user.player.PlayerEnt;
import com.kingston.jforgame.server.logs.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户数据异步持久化的服务
 * 
 * @author kingston
 */
public class DbService {

	private static Logger logger = LoggerFactory.getLogger(SqlFactory.class);

	private static volatile DbService instance = new DbService();

	public static DbService getInstance() {
		return instance;
	}

	private Worker playerWorker = new Worker();

	private Worker commonWorker = new Worker();

	/**
	 * start consumer thread
	 */
	public void init() {
		new NamedThreadFactory("db-player-service").newThread(playerWorker).start();
		new NamedThreadFactory("db-common-service").newThread(commonWorker).start();
	}

	private final AtomicBoolean run = new AtomicBoolean(true);

	/**
	 * 自动插入或者更新数据
	 * 
	 * @param entity
	 */
	public void insertOrUpdate(BaseEntity entity) {
		if (entity instanceof PlayerEnt) {
			playerWorker.addToQueue(entity);
		} else {
			commonWorker.addToQueue(entity);
		}
	}

	/**
	 * 仅更新部分字段
	 * @param entity
	 * @param columns
	 */
	public void saveColumns(BaseEntity entity, String... columns) {
		entity.savingColumns().add(Arrays.stream(columns).collect(Collectors.joining()));
		insertOrUpdate(entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param entity
	 */
	public void delete(BaseEntity entity) {
		entity.setDelete();
		if (entity instanceof PlayerEnt) {
			playerWorker.addToQueue(entity);
		} else {
			commonWorker.addToQueue(entity);
		}
	}

	private class Worker implements Runnable {

		private BlockingQueue<BaseEntity> queue = new BlockingUniqueQueue<>();

		void addToQueue(BaseEntity ent) {
			this.queue.add(ent);
		}

		@Override
		public void run() {
			while (run.get()) {
				BaseEntity entity = null;
				try {
					entity = queue.take();
					saveToDb(entity);
				} catch (Exception e) {
					LoggerUtils.error("", e);
					// 有可能是并发抛错，重新放入队列
					insertOrUpdate(entity);
				}
			}
		}

		void shutDown() {
			for (;;) {
				if (!queue.isEmpty()) {
					saveAllBeforeShutDown();
				} else {
					break;
				}
			}
		}

		void saveAllBeforeShutDown() {
			while (!queue.isEmpty()) {
				Iterator<BaseEntity> it = queue.iterator();
				while (it.hasNext()) {
					BaseEntity next = it.next();
					it.remove();
					saveToDb(next);
				}
			}
		}
	}

	/**
	 * 数据真正持久化
	 * 
	 * @param entity
	 */
	private void saveToDb(BaseEntity entity) {
		entity.doBeforeSave();
		String saveSql = entity.getSaveSql();
		if (logger.isDebugEnabled()) {
			logger.debug("sql={}", saveSql);
		}
		try {
			if (DbUtils.executeUpdate(saveSql) > 0) {
				entity.resetDbStatus();
			}
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public void shutDown() {
		run.getAndSet(false);
		playerWorker.shutDown();
		commonWorker.shutDown();

		LoggerUtils.error("[Db4Common] 执行全部命令后关闭");
	}

}
