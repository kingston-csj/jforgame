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
 * Persistence in queue form
 */
public class QueueContainer extends BasePersistContainer {

    private final BlockingQueue<Entity<?>> queue = new LinkedBlockingDeque<>();

    /**
     * Current saving queue, deduplicated
     */
    private final Set<String> savingQueue = new ConcurrentHashSet<>();

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("jforgame-persist-queue-service");

    /**
     * Last error log print time
     */
    private long lastErrorTime = 0;

    public QueueContainer(String name, SavingStrategy savingStrategy) {
        this.name = name;
        this.savingStrategy = savingStrategy;
        // Start thread to run
        namedThreadFactory.newThread(this::run).start();
    }

    @Override
    public void receive(Entity<?> entity) {
        String key = entity.getKey();
        if (!run.get()) {
            // Shop is closed, sorry no service
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
                // Possibly concurrent error, put back into queue
                if (entity != null) {
                    receive(entity);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Other exceptions, putting back into queue still can't solve the problem, problematic ones will still have problems
                // Here needs a strong notification operation, for example notify developers to fix bug
                if (entity != null) {
                    receive(entity);
                }
                // Repeatedly putting into persistence queue can easily cause exception log explosion, control log frequency here
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
