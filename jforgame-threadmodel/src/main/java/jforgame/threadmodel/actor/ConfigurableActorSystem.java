package jforgame.threadmodel.actor;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.threadmodel.ThreadModel;
import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.config.DispatcherConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigurableActorSystem implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurableActorSystem.class);

    final AtomicBoolean running = new AtomicBoolean(true);

    private final ActorSystemConfig systemConfig;

    private final ThreadPoolExecutor threadPool;

    private final Map<String, Actor> actors = new ConcurrentHashMap<>();

    private final SharedActor sharedActor;

    public ConfigurableActorSystem() {
        this(new ActorSystemConfig());
    }

    public ConfigurableActorSystem(ActorSystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        
        // 根据配置创建线程池
        DispatcherConfig dispatcherConfig = systemConfig.getDefaultDispatcher();
        NamedThreadFactory threadFactory = new NamedThreadFactory("configurable-actor-system");
        
        this.threadPool = new ThreadPoolExecutor(
                dispatcherConfig.getCorePoolSize(),
                dispatcherConfig.getMaxPoolSize(),
                dispatcherConfig.getKeepAliveTime().getSeconds(),
                TimeUnit.SECONDS,
                createTaskQueue(dispatcherConfig),
                threadFactory
        );
        
        // 创建共享Actor组
        Actor[] actorGroup = new Actor[2]; // 默认2个共享Actor
        for (int i = 0; i < actorGroup.length; i++) {
            actorGroup[i] = new ConfigurableActor(this, "/system/shared-actor-" + i, systemConfig);
        }
        sharedActor = new SharedActor(actorGroup);
        
        logger.info("ConfigurableActorSystem initialized with config: {}", systemConfig);
    }

    private LinkedBlockingQueue<Runnable> createTaskQueue(DispatcherConfig config) {
        int queueSize = config.getTaskQueueSize();
        if (queueSize <= 0) {
            return new LinkedBlockingQueue<>();
        } else {
            return new LinkedBlockingQueue<>(queueSize);
        }
    }

    public Actor createActor(String actorPath) {
        if (actors.containsKey(actorPath)) {
            throw new IllegalArgumentException("Actor already exists: " + actorPath);
        }
        
        ConfigurableActor actor = new ConfigurableActor(this, actorPath, systemConfig);
        actors.put(actorPath, actor);
        
        logger.info("Created actor: {}", actorPath);
        return actor;
    }

    public Actor getActor(String actorPath) {
        return actors.get(actorPath);
    }

    public void removeActor(String actorPath) {
        Actor removed = actors.remove(actorPath);
        if (removed != null) {
            logger.info("Removed actor: {}", actorPath);
        }
    }

    public Actor getOrCreateActor(String actorPath) {
        return actors.computeIfAbsent(actorPath, path -> {
            ConfigurableActor actor = new ConfigurableActor(this, path, systemConfig);
            logger.info("Auto-created actor: {}", path);
            return actor;
        });
    }

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
            logger.info("开始关闭ConfigurableActorSystem...");
            try {
                // 清空Actor注册表
                actors.clear();
                
                // 关闭业务执行器
                threadPool.shutdown();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
                
                logger.info("ConfigurableActorSystem关闭完成");
            } catch (Exception e) {
                logger.error("ConfigurableActorSystem关闭异常", e);
            }
        }
    }

    public ActorSystemConfig getSystemConfig() {
        return systemConfig;
    }

    public Map<String, Actor> getActors() {
        return actors;
    }
}
