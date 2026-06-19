package jforgame.commons.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base persistence container, provides some basic functionality
 */
public abstract class BasePersistContainer implements PersistContainer {

    protected static final Logger logger = LoggerFactory.getLogger(BasePersistContainer.class);
    /**
     * Container name, used when logging
     */
    protected String name;

    /**
     * Persistence strategy
     */
    protected SavingStrategy savingStrategy;

    /**
     * Whether running, true means running, false means closed
     * When container closes, use this state to stop accepting new elements
     */
    protected final AtomicBoolean run = new AtomicBoolean(true);

    @Override
    public void shutdownGraceful() {
        run.compareAndSet(true, false);
        try {
            saveAllBeforeShutdown();
        } catch (Exception e) {
            // Error here can only be logged, because server is shutting down
            logger.error("PersistContainer[{}] shutdown error, queue size is [{}]", name, size(), e);
        }
        logger.info("db container [{}] close ok", name);
    }

    /**
     * Before shutdown, save all elements in queue
     */
    protected abstract void saveAllBeforeShutdown();

}
