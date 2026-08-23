package jforgame.commons.persist;

import jforgame.commons.persist.dlq.DeadLetterQueue;
import jforgame.commons.thread.NamedThreadFactory;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Delay persistence container
 * Built-in private local cache, compatible with both Caffeine in-process cache and Redis out-of-process cache
 * No external cache dependency, no extra interface, deduplicate by key on single machine
 * Refer to QueueContainer design: shared schedule thread, independent blocking persist worker thread
 */
public class DelayContainer extends BasePersistContainer {

    // Global shared scheduled thread pool for countdown only, shared across all DelayContainer instances
    private static final ScheduledExecutorService SHARED_TIMER = Executors.newScheduledThreadPool(1, new NamedThreadFactory("jforgame-persist-delay-timer"));

    // Persist task queue for decoupling schedule thread and persist thread
    private final BlockingQueue<PersistTask> persistQueue = new LinkedBlockingQueue<>();

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("jforgame-persist-delay-service");

    // Store pending scheduled tasks: key -> Node
    private final ConcurrentHashMap<String, Node> pool = new ConcurrentHashMap<>();
    // Cache latest entity to adapt in-process & remote cache
    private final ConcurrentHashMap<String, Entity<?>> localLatestEntityMap = new ConcurrentHashMap<>();

    // Timestamp for error log frequency control
    private volatile long lastErrorTime = 0;
    // Persist delay second
    private final int delaySeconds;

    public DelayContainer(String name, int delaySeconds, SavingStrategy savingStrategy) {
        this(name, delaySeconds, savingStrategy, null);
    }

    /**
     * Construct with dead letter queue support
     *
     * @param name            container name
     * @param delaySeconds    persist delay in seconds
     * @param savingStrategy  saving strategy
     * @param deadLetterQueue dead letter queue manager, null means retry forever (no DLQ)
     */
    public DelayContainer(String name, int delaySeconds, SavingStrategy savingStrategy, DeadLetterQueue deadLetterQueue) {
        this.name = name;
        this.delaySeconds = delaySeconds;
        this.savingStrategy = savingStrategy;
        this.deadLetterQueue = deadLetterQueue;
        // Start independent persist worker thread, consistent with QueueContainer startup mode
        namedThreadFactory.newThread(this::persistWorker).start();
    }

    @Override
    public void receive(Entity<?> entity) {
        if (entity == null) {
            return;
        }
        final String key = entity.getKey();

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

        // Always refresh latest entity
        localLatestEntityMap.put(key, entity);

        Node newNode = new Node(key, this);
        Node existNode = pool.putIfAbsent(key, newNode);
        if (existNode != null) {
            return;
        }

        SHARED_TIMER.schedule(newNode, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Timer callback: submit persist task to queue, schedule thread released quickly
     */
    private static final class Node implements Runnable {
        private final String key;
        private final DelayContainer container;

        public Node(String key, DelayContainer container) {
            this.key = key;
            this.container = container;
        }

        @Override
        public void run() {
            String k = key;
            Entity<?> latestEntity = container.localLatestEntityMap.get(k);
            if (latestEntity == null) {
                container.pool.remove(k, this);
                return;
            }
            // Hand over persist work to queue, schedule thread will not block
            container.persistQueue.offer(new PersistTask(k, latestEntity, this));
        }
    }

    /**
     * Persist task carrier
     */
    private static class PersistTask {
        final String key;
        final Entity<?> entity;
        final Node bindNode;

        PersistTask(String key, Entity<?> entity, Node bindNode) {
            this.key = key;
            this.entity = entity;
            this.bindNode = bindNode;
        }
    }

    /**
     * Independent persist thread, consistent with QueueContainer single thread loop
     */
    private void persistWorker() {
        while (run.get()) {
            PersistTask task = null;
            try {
                task = persistQueue.poll(1, TimeUnit.SECONDS);
                if (task == null) {
                    continue;
                }
                String key = task.key;
                Entity<?> latestEntity = localLatestEntityMap.get(key);
                if (latestEntity == null) {
                    continue;
                }

                savingStrategy.doSave(latestEntity);

                // CAS clean cache
                boolean removeSuccess = pool.remove(key, task.bindNode);
                if (removeSuccess) {
                    localLatestEntityMap.remove(key, latestEntity);
                }
                handleSaveSuccess(key);
            } catch (ConcurrentModificationException ignored) {
                persistQueue.offer(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                if (task != null) {
                    // Check if entity should be moved to dead letter queue
                    Entity<?> failedEntity = localLatestEntityMap.get(task.key);
                    boolean movedToDeadLetter = handleSaveFailure(task.key, failedEntity, e);
                    if (!movedToDeadLetter) {
                        // Re-enqueue for retry
                        persistQueue.offer(task);
                    }
                }
                long now = System.currentTimeMillis();
                if (now - lastErrorTime > 5000) {
                    lastErrorTime = now;
                    logger.error("persist exception, key:{}, poolSize:{}", task.key, pool.size(), e);
                }
            }
        }
    }

    /**
     * Fast shutdown, no waiting, consistent with QueueContainer shutdown logic
     */
    @Override
    protected void saveAllBeforeShutdown() {
        run.set(false);
        int poolSize = pool.size();
        Iterator<Map.Entry<String, Node>> iterator = pool.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> entry = iterator.next();
            String key = entry.getKey();
            Entity<?> entity = localLatestEntityMap.get(key);
            if (entity != null) {
                try {
                    savingStrategy.doSave(entity);
                } catch (Exception e) {
                    logger.error("shutdown persist fail, key:{}", key, e);
                }
            }
            iterator.remove();
            localLatestEntityMap.remove(key);
        }

        // Clear residual persist tasks
        persistQueue.clear();
        logger.error("{} container shutdown, save {} elements", name, poolSize);

        try {
            shutdownScheduler();
        } catch (Exception e) {
            logger.error("exception occurs while closing scheduler on shutdown", e);
        }
    }

    @Override
    public int size() {
        return pool.size();
    }

    public void shutdownScheduler() {
        SHARED_TIMER.shutdown();
    }

    @Override
    public String toString() {
        long pendingTaskCount = pool.size();
        long cachedEntityCount = localLatestEntityMap.size();
        return String.format("DelayContainer{name='%s', delaySeconds=%d, pendingScheduledTasks=%d, cachedLatestEntityNum=%d, running=%s}", this.name, this.delaySeconds, pendingTaskCount, cachedEntityCount, run.get());
    }
}