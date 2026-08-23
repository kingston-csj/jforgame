package jforgame.commons.persist;


import jforgame.commons.persist.dlq.DeadLetterQueue;
import jforgame.commons.thread.NamedThreadFactory;
import jforgame.commons.util.TimeUtil;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Persistence queue container supporting both in-process and out-of-process cache
 * Queue only stores keys; Map caches the latest entity to avoid old reference issues
 */
public class QueueContainer extends BasePersistContainer {

    // Queue only stores entity unique keys
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    /**
     * Key -> latest entity cache for deduplication & latest data fetch
     * Compatible with Caffeine reused object and Redis brand-new entity object
     */
    private final Map<String, Entity<?>> savingQueue = new ConcurrentHashMap<>();

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("jforgame-persist-queue-service");

    /**
     * Last error log timestamp for log throttling
     */
    private long lastErrorTime = 0;

    public QueueContainer(String name, SavingStrategy savingStrategy) {
        this(name, savingStrategy, null);
    }

    /**
     * Construct with dead letter queue support
     *
     * @param name            container name
     * @param savingStrategy  saving strategy
     * @param deadLetterQueue dead letter queue manager, null means retry forever (no DLQ)
     */
    public QueueContainer(String name, SavingStrategy savingStrategy, DeadLetterQueue deadLetterQueue) {
        this.name = name;
        this.savingStrategy = savingStrategy;
        this.deadLetterQueue = deadLetterQueue;
        namedThreadFactory.newThread(this::run).start();
    }

    @Override
    public void receive(Entity<?> entity) {
        String key = entity.getKey();
        if (!run.get()) {
            logger.info("db closed, received entity key: {}", key);
            return;
        }

        // Reject entities whose keys are already in dead letter queue,
        // preventing infinite retry loops. But keep the latest entity snapshot
        // in the dead letter for future reprocessing via deadLetterQueue.reprocess().
        if (isDeadKey(key)) {
            handleDeadEntityUpdate(key, entity);
            logger.warn("Entity rejected: key [{}] is in dead letter queue, use deadLetterQueue.reprocess() instead", key);
            return;
        }

        // Judge whether this key is newly added to the map
        boolean isNewKey = savingQueue.putIfAbsent(key, entity) == null;
        // Force overwrite to latest entity, cover old entity data
        savingQueue.put(key, entity);
        // Only add key to queue when first time entering map
        if (isNewKey) {
            queue.add(key);
        }
    }

    private void run() {
        while (run.get()) {
            String key = null;
            try {
                key = queue.poll(1, TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }

                // Fetch the newest entity cached
                Entity<?> latestEntity = savingQueue.get(key);
                if (latestEntity == null) {
                    continue;
                }

                savingStrategy.doSave(latestEntity);
                // Remove cache only if current key still maps to this entity
                savingQueue.remove(key, latestEntity);
                handleSaveSuccess(key);

            } catch (ConcurrentModificationException e1) {
                if (key != null) {
                    // Re-enqueue key for retry
                    queue.offer(key);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                if (key != null) {
                    // Check if entity should be moved to dead letter queue
                    Entity<?> failedEntity = savingQueue.get(key);
                    boolean movedToDeadLetter = handleSaveFailure(key, failedEntity, e);
                    if (!movedToDeadLetter) {
                        // Re-enqueue for retry
                        queue.offer(key);
                    }
                }
                // Control error log frequency (5 minutes interval)
                long now = System.currentTimeMillis();
                if (now - lastErrorTime > 5 * TimeUtil.MILLIS_PER_MINUTE) {
                    lastErrorTime = now;
                    logger.error("save entity error, key: {}, queue size: {}", key, queue.size(), e);
                }
            }
        }
    }

    /**
     * Fast shutdown: block new requests, flush all remaining keys synchronously
     */
    @Override
    protected void saveAllBeforeShutdown() {
        run.set(false);
        String key;
        int poolSize = queue.size();
        while ((key = queue.poll()) != null) {
            Entity<?> entity = savingQueue.get(key);
            if (entity == null) {
                continue;
            }
            try {
                savingStrategy.doSave(entity);
            } catch (Exception e) {
                logger.error("shutdown persist fail, key:{}", key, e);
            }
        }
        // Clear residual cache
        savingQueue.clear();
        logger.error("{} container shutdown, save {} elements", name, poolSize);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return String.format(
                "QueueContainer{name='%s', queuePendingKeys=%d, cachedEntityNum=%d, isRunning=%b}",
                name,
                queue.size(),
                savingQueue.size(),
                run.get()
        );
    }
}