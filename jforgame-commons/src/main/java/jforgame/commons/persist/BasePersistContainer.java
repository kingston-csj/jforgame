package jforgame.commons.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * PersistContainer的基类
 */
public abstract class BasePersistContainer implements PersistContainer {

    protected static final Logger logger = LoggerFactory.getLogger(PersistContainer.class);
    /**
     * 容器名称，日志打印时使用
     */
    protected String name;

    /**
     * 持久化策略
     */
    protected SavingStrategy savingStrategy;

    protected final AtomicBoolean run = new AtomicBoolean(true);


    @Override
    public void shutdownGraceful() {
        run.compareAndSet(true, false);
        saveAllBeforeShutdown();
        logger.info("db container [{}] close ok", name);
    }

    /**
     * 停服前，保存所有数据
     */
    protected abstract void saveAllBeforeShutdown();

}
