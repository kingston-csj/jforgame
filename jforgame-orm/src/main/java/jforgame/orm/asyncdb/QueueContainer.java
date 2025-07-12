package jforgame.orm.asyncdb;


import jforgame.commons.TimeUtil;
import jforgame.commons.ds.ConcurrentHashSet;
import jforgame.commons.thread.NamedThreadFactory;
import jforgame.orm.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 以队列的形式持久化
 */
public class QueueContainer implements PersistContainer {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private final AtomicBoolean run = new AtomicBoolean(true);

    private final String name;

    private final BlockingQueue<BaseEntity<?>> queue = new LinkedBlockingDeque<>();

    private final Set<String> savingQueue = new ConcurrentHashSet<>();

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("jforgame-persist-queue-thread");

    private SavingStrategy savingStrategy;

    /**
     * 上次错误日志打印的时间
     */
    private long lastErrorTime = 0;

    public QueueContainer(String name, SavingStrategy savingStrategy) {
        // 启动线程跑
        namedThreadFactory.newThread(this::run).start();
        this.name = name;
        this.savingStrategy = savingStrategy;
    }

    @Override
    public void receive(BaseEntity<?> entity) {
        String key = entity.getKey();
        if (savingQueue.contains(key)) {
            return;
        }
        this.queue.add(entity);
        this.savingQueue.add(key);
    }

    private void run() {
        while (run.get()) {
            BaseEntity<?> entity = null;
            try {
                entity = queue.take();
                savingQueue.remove(entity.getKey());
                savingStrategy.doSave(entity);
            } catch (ConcurrentModificationException e1) {
                // 有可能是并发抛错，重新放入队列
                receive(entity);
            } catch (Exception e) {
                // 其他异常，重复放入队列，还是无法解决问题，有问题的终究有问题
                // 这里需要一个强通知操作，例如通知开发人员，起来修bug了
                receive(entity);
                // 重复放入持久化队列，很容易造成异常日志爆炸了，这里控制下日志频率
                if (System.currentTimeMillis() - lastErrorTime > 5 * TimeUtil.MILLIS_PER_MINUTE) {
                    lastErrorTime = System.currentTimeMillis();
                    logger.error("save entity error, entity: {}, queue size: {}", entity, queue.size(), e);
                }
            }
        }
    }

    @Override
    public void shutdownGraceful() {
        run.compareAndSet(true, false);
        for (; ; ) {
            if (!queue.isEmpty()) {
                saveAllBeforeShutDown();
            } else {
                break;
            }
        }
        logger.info("db container [{}] close ok", name);
    }

    @Override
    public SavingStrategy getSavingStrategy() {
        return savingStrategy;
    }

    private void saveAllBeforeShutDown() {
        try {
            while (!queue.isEmpty()) {
                Iterator<BaseEntity<?>> it = queue.iterator();
                while (it.hasNext()) {
                    BaseEntity<?> ent = it.next();
                    it.remove();
                    savingStrategy.doSave(ent);
                }
            }
        } catch (Exception e) {
            // 这里报错，就只能打日志了，因为要关服了
            logger.error("save all entity error, queue size: {}", queue.size(), e);
        }
    }
}
