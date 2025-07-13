package jforgame.commons.persist;

import jforgame.commons.TimeUtil;
import jforgame.commons.ds.ConcurrentHashSet;
import jforgame.commons.thread.NamedThreadFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 以延迟执行的形式持久化
 */
public class DelayContainer extends BasePersistContainer {

    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new NamedThreadFactory("jforgame-persist-delay-service"));

    /**
     * 当前正在保存的队列，去重
     */
    private final Set<String> savingQueue = new ConcurrentHashSet<>();

    /**
     * db容器器排队的任务池
     */
    private final ConcurrentMap<String, Node> pool = new ConcurrentHashMap<>();

    /**
     * 上次错误日志打印的时间
     */
    private long lastErrorTime = 0;
    /**
     * 延迟秒数
     */
    private final int delaySeconds;


    public DelayContainer(String name, int delaySeconds, SavingStrategy savingStrategy) {
        this.name = name;
        this.delaySeconds = delaySeconds;
        this.savingStrategy = savingStrategy;
    }

    public void receive(Entity<?> entity) {
        if (!run.get()) {
            // 小店已经打烊了，恕不招待
            return;
        }
        String key = entity.getKey();
        if (savingQueue.contains(key)) {
            return;
        }
        Runnable task = () -> {
            try {
                savingStrategy.doSave(entity);
                pool.remove(key);
                savingQueue.remove(key);
            } catch (Exception e) {
                receive(entity);
                // 重复放入持久化队列，很容易造成异常日志爆炸了，这里控制下日志频率
                if (System.currentTimeMillis() - lastErrorTime > 5 * TimeUtil.MILLIS_PER_MINUTE) {
                    lastErrorTime = System.currentTimeMillis();
                    logger.error("save entity error, entity: {}, pool size: {}", entity, pool.size(), e);
                }
            }
        };
        Node node = new Node();
        node.key = key;
        node.task = task;

        pool.put(key, node);

        service.schedule(node, delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    protected void saveAllBeforeShutdown() {
        Iterator<Map.Entry<String, Node>> iterator = pool.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Node> next = iterator.next();
            // 直接执行
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

}