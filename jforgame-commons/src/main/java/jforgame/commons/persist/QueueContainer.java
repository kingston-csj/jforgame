package jforgame.commons.persist;


import jforgame.commons.util.TimeUtil;
import jforgame.commons.ds.ConcurrentHashSet;
import jforgame.commons.thread.NamedThreadFactory;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 以队列的形式持久化
 */
public class QueueContainer extends BasePersistContainer {

    private final BlockingQueue<Entity<?>> queue = new LinkedBlockingDeque<>();

    /**
     * 当前正在保存的队列，去重
     */
    private final Set<String> savingQueue = new ConcurrentHashSet<>();

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("jforgame-persist-queue-service");

    /**
     * 上次错误日志打印的时间
     */
    private long lastErrorTime = 0;

    public QueueContainer(String name, SavingStrategy savingStrategy) {
        this.name = name;
        this.savingStrategy = savingStrategy;
        // 启动线程跑
        namedThreadFactory.newThread(this::run).start();
    }

    @Override
    public void receive(Entity<?> entity) {
        String key = entity.getKey();
        if (!run.get()) {
            // 小店已经打烊了，恕不招待
            logger.info("db closed, received entity: {}", key);
            return;
        }
        if (!savingQueue.add(key)) {
            return;
        }
        this.queue.add(entity);
    }

    private void run() {
        while (run.get()) {
            Entity<?> entity = null;
            try {
                entity = queue.poll(1, TimeUnit.SECONDS);
                if (entity == null) {
                    continue;
                }
                savingQueue.remove(entity.getKey());
                savingStrategy.doSave(entity);
            } catch (ConcurrentModificationException e1) {
                // 有可能是并发抛错，重新放入队列
                if (entity != null) {
                    receive(entity);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // 其他异常，重复放入队列，还是无法解决问题，有问题的终究有问题
                // 这里需要一个强通知操作，例如通知开发人员，起来修bug了
                if (entity != null) {
                    receive(entity);
                }
                // 重复放入持久化队列，很容易造成异常日志爆炸了，这里控制下日志频率
                if (System.currentTimeMillis() - lastErrorTime > 5 * TimeUtil.MILLIS_PER_MINUTE) {
                    lastErrorTime = System.currentTimeMillis();
                    logger.error("save entity error, entity: {}, queue size: {}", entity, queue.size(), e);
                }
            }
        }
    }

    @Override
    protected void saveAllBeforeShutdown() {
        Entity<?> ent;
        while ((ent = queue.poll()) != null) {
            try {
                savingStrategy.doSave(ent);
            } catch (Exception e) {
                logger.error("save entity error, entity: {}", ent, e);
            }
        }
    }

    @Override
    public int size() {
        return queue.size();
    }
}
