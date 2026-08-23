package jforgame.commons.persist;

import jforgame.commons.persist.dlq.DeadLetterQueue;
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

    /**
     * Dead letter queue manager, null means dead letter mechanism disabled (retry forever).
     * Subclasses should call {@link #handleSaveSuccess},
     * {@link #handleSaveFailure} to integrate with DLQ.
     */
    protected DeadLetterQueue deadLetterQueue;

    /**
     * Reset retry count on save success, no-op when DLQ disabled
     *
     * @param key entity key
     */
    protected void handleSaveSuccess(String key) {
        if (deadLetterQueue != null) {
            deadLetterQueue.onSaveSuccess(key);
        }
    }

    /**
     * Handle save failure, decide whether to move entity to dead letter queue.
     * When DLQ disabled, always returns false (should retry).
     *
     * @param key    entity key
     * @param entity the entity that failed
     * @param ex     the exception thrown by saving strategy
     * @return true if moved to dead letter queue (should not retry), false if should retry
     */
    protected boolean handleSaveFailure(String key, Entity<?> entity, Exception ex) {
        if (deadLetterQueue == null) {
            return false;
        }
        return deadLetterQueue.onSaveFailure(key, entity, ex);
    }

    /**
     * Check whether a key is in the dead letter queue.
     * When DLQ disabled (deadLetterQueue is null), always returns false.
     * Subclasses should call this at the beginning of {@code receive()} to reject
     * entities whose keys are already dead, preventing infinite retry loops.
     *
     * @param key entity key
     * @return true if the key is in dead letter queue and should be rejected
     */
    protected boolean isDeadKey(String key) {
        if (deadLetterQueue == null) {
            return false;
        }
        return deadLetterQueue.isDead(key);
    }

    /**
     * Update the entity snapshot in the dead letter queue without re-enqueuing.
     * Call this when a newer version of a dead entity is received by {@code receive()}:
     * the entity state is updated but the key is NOT re-enqueued for persistence.
     *
     * @param key    entity key
     * @param entity the latest entity state
     */
    protected void handleDeadEntityUpdate(String key, Entity<?> entity) {
        if (deadLetterQueue != null) {
            deadLetterQueue.updateDeadEntity(key, entity);
        }
    }

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
