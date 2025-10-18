package jforgame.threadmodel.actor;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.threadmodel.ThreadModel;
import jforgame.threadmodel.actor.config.ActorSystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * actor线程模型
 * 负责actor调度
 * 如果选择将创建的actor注册到该容器，需要自行管理actor的生命周期，请及时调用{@link #removeActor(String)}，避免内存泄露
 */
public class ActorSystem implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(ActorSystem.class);

    final AtomicBoolean running = new AtomicBoolean(true);

    private final ActorSystemConfig systemConfig;

    private final ThreadPoolExecutor threadPool;

    /**
     * 所有注册的actor（注册后，请自行管理，避免内存泄露）
     */
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();

    private final SharedActor sharedActor;

    public ActorSystem() {
        this(new ActorSystemConfig());
    }

    public ActorSystem(ActorSystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        // 根据配置创建线程池
        NamedThreadFactory threadFactory = new NamedThreadFactory("actor-system");
        this.threadPool = new ThreadPoolExecutor(
                systemConfig.getCorePoolSize(),
                systemConfig.getMaxPoolSize(),
                systemConfig.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory
        );

        // 创建共享Actor组
        Actor[] actorGroup = new Actor[2]; // 默认2个共享Actor
        for (int i = 0; i < actorGroup.length; i++) {
            actorGroup[i] = new BaseActor(this, "/system/shared-actor-" + i);
        }
        sharedActor = new SharedActor(actorGroup);
    }


    public Actor createActor(String actorPath) {
        if (actors.containsKey(actorPath)) {
            throw new IllegalArgumentException("Actor already exists: " + actorPath);
        }

        BaseActor actor = new BaseActor(this, actorPath);
        actors.put(actorPath, actor);
        return actor;
    }

    public Actor getActor(String actorPath) {
        return actors.get(actorPath);
    }

    /**
     * 移除指定的actor
     *
     * @param actorPath actor路径
     */
    public void removeActor(String actorPath) {
        Actor removed = actors.remove(actorPath);
    }

    /**
     * 获取或创建actor
     *
     * @param actorPath actor路径
     * @return actor
     */
    public Actor getOrCreateActor(String actorPath) {
        return actors.computeIfAbsent(actorPath, path -> new BaseActor(this, path));
    }

    /**
     * 绑定共享actor
     *
     * @param key 共享actor键值
     * @return 共享actor
     */
    public Actor bindingSharedActor(long key) {
        return sharedActor.getSharedActor(key);
    }

    @Override
    public void accept(Runnable task) {
        threadPool.submit(task);
    }

    @Override
    public void shutDown() {
        if (running.compareAndSet(true, false)) {
            logger.info("开始关闭ActorSystem...");
            try {
                // 清空Actor注册表
                actors.clear();

                // 关闭业务执行器
                threadPool.shutdown();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }

                logger.info("ActorSystem关闭完成");
            } catch (Exception e) {
                logger.error("ActorSystem关闭异常", e);
            }
        }
    }

    public ActorSystemConfig getSystemConfig() {
        return systemConfig;
    }

    public Map<String, Actor> getActors() {
        return actors;
    }

    @Override
    public boolean isShutdown() {
        return !running.get();
    }

    /**
     * 获取统计信息
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActorSystem Statistics:\n");
        sb.append("Root Queue Current Task Size: ").append(threadPool.getQueue().size()).append("\n");
        sb.append("Root Queue finished Task Size: ").append(threadPool.getTaskCount()).append("\n");
        sb.append("Shared Queue Workers: ").append(sharedActor.getWorkerSize()).append("\n");

        Actor[] boxGroup = sharedActor.getGroup();
        for (int i = 0; i < boxGroup.length; i++) {
            sb.append("Shared Worker[").append(i).append("]: ").append(boxGroup[i].getMailbox().getTaskSize()).append("\n");
        }
        return sb.toString();
    }
}
