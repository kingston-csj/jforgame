package jforgame.commons.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基础的持久化容器，提供了一些基础的功能
 */
public abstract class BasePersistContainer implements PersistContainer {

    protected static final Logger logger = LoggerFactory.getLogger(BasePersistContainer.class);
    /**
     * 容器名称，日志打印时使用
     */
    protected String name;

    /**
     * 持久化策略
     */
    protected SavingStrategy savingStrategy;

    /**
     * 是否运行，true 表示运行，false 表示关闭
     * 容器关闭时，使用此状态，停止接受新元素
     */
    protected final AtomicBoolean run = new AtomicBoolean(true);

    @Override
    public void shutdownGraceful() {
        run.compareAndSet(true, false);
        try {
            saveAllBeforeShutdown();
        } catch (Exception e) {
            // 这里报错，就只能打日志了，因为要关服了
            logger.error("PersistContainer[{}] shutdown error, queue size is [{}]", name, size(), e);
        }
        logger.info("db container [{}] close ok", name);
    }

    /**
     * 关闭之前，保存所有在队列里的元素
     */
    protected abstract void saveAllBeforeShutdown();

}
