package jforgame.commons.persist.dlq;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.SavingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Dead letter queue manager, encapsulates retry counting, dead letter storage and listener notification.
 * <p>
 * Pass an instance to a persist container to enable the dead letter mechanism.
 * When an entity fails to persist beyond {@link #maxRetryCount}, it is moved to the dead letter queue
 * and registered {@link DeadLetterListener}s are notified for developer feedback.
 * <p>
 * Pass {@code null} to a container to disable the dead letter mechanism (retry forever,
 * backward compatible with old behavior).
 * <p>
 * A single {@code DeadLetterQueue} instance can be shared by multiple containers, or each
 * container can hold its own instance.
 */
public class DeadLetterQueue {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueue.class);

    /**
     * Max retry count before moving entity to dead letter queue.
     * 0 or negative means retry forever (never move to dead letter queue).
     */
    private volatile int maxRetryCount;

    /**
     * Retry count per entity key
     */
    private final ConcurrentHashMap<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    /**
     * Dead letter storage: key -> DeadLetter
     */
    private final ConcurrentHashMap<String, DeadLetter> deadLetterMap = new ConcurrentHashMap<>();

    /**
     * Dead letter listeners
     */
    private final List<DeadLetterListener> listeners = new CopyOnWriteArrayList<>();

    public DeadLetterQueue(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Reset retry count on save success
     *
     * @param key entity key
     */
    public void onSaveSuccess(String key) {
        retryCountMap.remove(key);
    }

    /**
     * Handle save failure. Increments retry count and checks if entity should move to dead letter queue.
     *
     * @param key    entity key
     * @param entity the entity that failed
     * @param ex     the exception thrown by saving strategy
     * @return true if moved to dead letter queue (should not retry), false if should retry
     */
    public boolean onSaveFailure(String key, Entity<?> entity, Exception ex) {
        int currentRetry = retryCountMap.getOrDefault(key, 0) + 1;

        // maxRetryCount <= 0 means retry forever, just track count
        if (maxRetryCount <= 0) {
            retryCountMap.put(key, currentRetry);
            return false;
        }

        if (currentRetry >= maxRetryCount) {
            // Move to dead letter queue
            DeadLetter deadLetter = new DeadLetter(
                    key, entity, currentRetry,
                    ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()
            );
            deadLetterMap.put(key, deadLetter);
            retryCountMap.remove(key);

            logger.error("Entity moved to dead letter queue, key: {}, retryCount: {}, error: {}",
                    key, currentRetry, ex.getMessage());

            // Notify listeners
            for (DeadLetterListener listener : listeners) {
                try {
                    listener.onDeadLetter(deadLetter);
                } catch (Exception le) {
                    logger.error("DeadLetterListener error for key: {}", key, le);
                }
            }
            return true;
        }

        retryCountMap.put(key, currentRetry);
        return false;
    }

    /**
     * Get all dead letters currently in the queue
     *
     * @return unmodifiable list of dead letters
     */
    public List<DeadLetter> getDeadLetters() {
        return Collections.unmodifiableList(new ArrayList<>(deadLetterMap.values()));
    }

    /**
     * Get dead letter count
     *
     * @return number of dead letters
     */
    public int getDeadLetterCount() {
        return deadLetterMap.size();
    }

    /**
     * Reprocess a dead letter by key, using the given saving strategy
     *
     * @param key      entity key
     * @param strategy saving strategy to re-save the entity
     * @return true if reprocess succeeded, false if key not found or save failed
     */
    public boolean reprocess(String key, SavingStrategy strategy) {
        DeadLetter deadLetter = deadLetterMap.remove(key);
        if (deadLetter == null) {
            return false;
        }
        Entity<?> entity = deadLetter.getEntity();
        boolean success = false;
        try {
            strategy.doSave(entity);
            success = true;
            logger.info("Dead letter reprocessed successfully, key: {}", key);
        } catch (Exception e) {
            logger.error("Dead letter reprocess failed, key: {}", key, e);
            // Put back to dead letter queue with incremented retry count
            deadLetterMap.put(key, new DeadLetter(
                    key, entity, deadLetter.getRetryCount() + 1,
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()
            ));
        }

        // Notify listeners
        for (DeadLetterListener listener : listeners) {
            try {
                listener.onDeadLetterReprocessed(deadLetter, success);
            } catch (Exception le) {
                logger.error("DeadLetterListener reprocess callback error for key: {}", key, le);
            }
        }
        return success;
    }

    /**
     * Batch reprocess all dead letters using the given saving strategy.
     * Iterates over all current dead letters, reprocesses each one, and returns a summary.
     *
     * @param strategy saving strategy to re-save entities
     * @return summary containing success count, failure count, and list of failed keys
     */
    public BatchReprocessResult batchReprocess(SavingStrategy strategy) {
        List<DeadLetter> snapshot = new ArrayList<>(deadLetterMap.values());
        int successCount = 0;
        int failCount = 0;
        List<String> failedKeys = new ArrayList<>();

        for (DeadLetter dl : snapshot) {
            boolean ok = reprocess(dl.getKey(), strategy);
            if (ok) {
                successCount++;
            } else {
                failCount++;
                failedKeys.add(dl.getKey());
            }
        }

        logger.info("Batch reprocess completed: total={}, success={}, fail={}",
                snapshot.size(), successCount, failCount);

        return new BatchReprocessResult(snapshot.size(), successCount, failCount, failedKeys);
    }

    /**
     * Reset all internal state: clears retry counts and all dead letters.
     * Use with caution — this permanently discards all unsaved entity snapshots
     * in the dead letter queue.
     */
    public void reset() {
        int deadLetterCount = deadLetterMap.size();
        retryCountMap.clear();
        deadLetterMap.clear();
        logger.warn("DeadLetterQueue reset: cleared {} dead letters and all retry state", deadLetterCount);
    }

    /**
     * Remove a dead letter by key
     *
     * @param key entity key
     * @return the removed dead letter, or null if not found
     */
    public DeadLetter removeDeadLetter(String key) {
        DeadLetter removed = deadLetterMap.remove(key);
        if (removed != null) {
            logger.info("Dead letter removed, key: {}", key);
        }
        return removed;
    }

    /**
     * Register a dead letter listener
     *
     * @param listener listener to register
     */
    public void addListener(DeadLetterListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a dead letter listener
     *
     * @param listener listener to remove
     */
    public void removeListener(DeadLetterListener listener) {
        listeners.remove(listener);
    }

    /**
     * Get max retry count
     *
     * @return max retry count, 0 or negative means retry forever
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * Set max retry count dynamically
     *
     * @param maxRetryCount max retry count before moving to dead letter queue,
     *                      0 or negative means retry forever
     */
    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Check whether the given key is currently in the dead letter queue.
     * Keys in dead letter queue are rejected by {@code receive()} to prevent
     * infinite retry loops. Use {@link #reprocess(String, SavingStrategy)}
     * to manually retry a dead letter after the root cause is fixed.
     *
     * @param key entity key
     * @return true if the key is in dead letter queue
     */
    public boolean isDead(String key) {
        return deadLetterMap.containsKey(key);
    }

    /**
     * Update the entity snapshot of an existing dead letter without re-enqueuing it.
     * This is called when a newer version of a dead entity is received by {@code receive()}:
     * the entity state is updated but the key is NOT re-enqueued for persistence,
     * preventing infinite retry loops while keeping the latest data available for recovery.
     *
     * @param key    entity key
     * @param entity the latest entity state to update the snapshot with
     */
    public void updateDeadEntity(String key, Entity<?> entity) {
        DeadLetter existing = deadLetterMap.get(key);
        if (existing == null) {
            return;
        }
        existing.setEntity(entity);
    }

    /**
     * Current pending retry count for a key (for monitoring/debugging)
     *
     * @param key entity key
     * @return current retry count, 0 if not present
     */
    public int getRetryCount(String key) {
        return retryCountMap.getOrDefault(key, 0);
    }

    @Override
    public String toString() {
        return String.format(
                "DeadLetterQueue{maxRetryCount=%d, pendingRetries=%d, deadLetterCount=%d, listeners=%d}",
                maxRetryCount, retryCountMap.size(), deadLetterMap.size(), listeners.size()
        );
    }

    /**
     * Result of a batch reprocess operation
     */
    public static class BatchReprocessResult {

        private final int totalCount;
        private final int successCount;
        private final int failCount;
        private final List<String> failedKeys;

        public BatchReprocessResult(int totalCount, int successCount, int failCount, List<String> failedKeys) {
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.failCount = failCount;
            this.failedKeys = failedKeys;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailCount() {
            return failCount;
        }

        public List<String> getFailedKeys() {
            return failedKeys;
        }

        public boolean allSuccess() {
            return failCount == 0;
        }

        @Override
        public String toString() {
            return String.format(
                    "BatchReprocessResult{total=%d, success=%d, fail=%d, failedKeys=%s}",
                    totalCount, successCount, failCount, failedKeys
            );
        }
    }
}
