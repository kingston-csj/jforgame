package jforgame.commons.persist;

import jforgame.commons.util.TimeUtil;
import jforgame.commons.thread.NamedThreadFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Persistence in delayed execution form
 */
public class DelayContainer extends BasePersistContainer {

    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new NamedThreadFactory("jforgame-persist-delay-service"));

    /**
     * DB container queued task pool
     */
    private final ConcurrentMap<String, Node> pool = new ConcurrentHashMap<>();

    /**
     * Last error log print time
     */
    private long lastErrorTime = 0;
    /**
     * Delay seconds
     */
    private final int delaySeconds;


    public DelayContainer(String name, int delaySeconds, SavingStrategy savingStrategy) {
        this.name = name;
        this.delaySeconds = delaySeconds;
        this.savingStrategy = savingStrategy;
    }

    public void receive(Entity<?> entity) {
        String key = entity.getKey();
        if (!run.get()) {
            // Shop is closed, sorry no service
            logger.info("db closed, received entity: {}", key);
            return;
        }
        Runnable task = () -> {
            try {
                savingStrategy.doSave(entity);
                pool.remove(key);
            } catch (Exception e) {
                pool.remove(key);
                receive(entity);
                // Repeatedly putting into persistence queue can easily cause exception log explosion, control log frequency here
                if (System.currentTimeMillis() - lastErrorTime > 5 * TimeUtil.MILLIS_PER_MINUTE) {
                    lastErrorTime = System.currentTimeMillis();
                    logger.error("save entity error, entity: {}, pool size: {}", entity, pool.size(), e);
                }
            }
        };
        Node node = new Node();
        node.key = key;
        node.task = task;

        Node existing = pool.putIfAbsent(key, node);
        if (existing != null) {
            return;
        }

        service.schedule(node, delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    protected void saveAllBeforeShutdown() {
        Iterator<Map.Entry<String, Node>> iterator = pool.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> next = iterator.next();
            // Execute directly
            next.getValue().task.run();
            iterator.remove();
        }
    }


    private static class Node implements Runnable {

        String key;

        Runnable task;

        @Override
        public void run() {
            task.run();
        }
    }

    @Override
    public int size() {
        return pool.size();
    }

}