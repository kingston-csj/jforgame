package jforgame.server.db;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.common.utils.BlockingUniqueQueue;
import jforgame.orm.SqlFactory;
import jforgame.server.logs.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用户数据异步持久化的服务
 *
 * @author kinson
 */
public class DbService {

    private static Logger logger = LoggerFactory.getLogger(SqlFactory.class);

    private static volatile DbService instance = new DbService();

    public static DbService getInstance() {
        return instance;
    }

    private Worker commonWorker = new Worker();

	/**
	 * 正在执行的db数量，用于抑制并发持久化数量
	 */
	private AtomicInteger savingDbCounter = new AtomicInteger();


	private int MAX_DB_COUNTER = 15;

    /**
     * start consumer thread
     */
    public void init() {
        new NamedThreadFactory("db-common-service").newThread(commonWorker).start();
    }

    private final AtomicBoolean run = new AtomicBoolean(true);

    /**
     * 自动插入或者更新数据
     *
     * @param entity
     */
    public void insertOrUpdate(BaseEntity entity) {
        // 防止重复添加
        if (entity.isSaving()) {
            return;
        }
        entity.setSaving();
        commonWorker.addToQueue(entity);
    }

    /**
     * 仅更新部分字段
     *
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
        commonWorker.addToQueue(entity);
    }

    private class Worker implements Runnable {

        private BlockingQueue<BaseEntity> queue = new BlockingUniqueQueue<>();

        void addToQueue(BaseEntity ent) {
            this.queue.add(ent);
        }

        @Override
        public void run() {
            while (run.get()) {
            	int size = queue.size();
                if (size <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {

                    }
                }
				for (int i = 0; i < size && savingDbCounter.getAndIncrement() < MAX_DB_COUNTER; i++) {
					BaseEntity entity = null;
					try {
						entity = queue.take();
						saveToDb(entity);
					} catch (Exception e) {
						LoggerUtils.error("", e);
					}
				}
            }
        }

        void shutDown() {
            for (; ; ) {
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
        entity.tell(() -> {
            try {
				entity.doBeforeSave();
				String saveSql = entity.getSaveSql();
				if (logger.isDebugEnabled()) {
					logger.debug("sql={}", saveSql);
				}
                if (DbUtils.executeUpdate(saveSql) > 0) {
                    entity.resetDbStatus();
                }
            } catch (Exception e) {
                LoggerUtils.error("", e);
				// 有可能是并发抛错，重新放入队列
				insertOrUpdate(entity);
            } finally {
				savingDbCounter.decrementAndGet();
			}
        });
    }

    public void shutDown() {
        run.getAndSet(false);
        commonWorker.shutDown();

        LoggerUtils.error("[Db4Common] 执行全部命令后关闭");
    }

}
