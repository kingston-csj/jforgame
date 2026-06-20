package jforgame.threadmodel.actor;

import jforgame.commons.thread.NamedThreadFactory;
import jforgame.threadmodel.ThreadModel;
import jforgame.threadmodel.actor.config.ActorSystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor thread model
 * Responsible for actor scheduling
 * If you choose to register created actors to this container, you need to manage the actor lifecycle yourself.
 * Please call {@link #removeActor(String)} in time to avoid memory leaks.
 */
public class ActorSystem implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(ActorSystem.class);

    final AtomicBoolean running = new AtomicBoolean(true);

    private final ActorSystemConfig systemConfig;

    private final ThreadPoolExecutor threadPool;

    /**
     * All registered actors (after registration, manage them yourself to avoid memory leaks)
     */
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();

    private final SharedActor sharedActor;

    public ActorSystem() {
        this(new ActorSystemConfig());
    }

    public ActorSystem(ActorSystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        // Create thread pool based on configuration
        NamedThreadFactory threadFactory = new NamedThreadFactory("actor-system");
        int queueCapacity = systemConfig.getQueueCapacity();
        LinkedBlockingQueue<Runnable> queue = queueCapacity > 0 ? new LinkedBlockingQueue<>(queueCapacity) : new LinkedBlockingQueue<>();
        this.threadPool = new ThreadPoolExecutor(
                systemConfig.getCorePoolSize(),
                systemConfig.getMaxPoolSize(),
                systemConfig.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                queue,
                threadFactory
        );

        // Create shared actor group
        int sharedActorCount = Math.max(1, systemConfig.getSystemSharedActorCount());
        Actor[] actorGroup = new Actor[sharedActorCount];
        for (int i = 0; i < actorGroup.length; i++) {
            actorGroup[i] = new BaseActor(this, "/system/shared-actor-" + i);
        }
        sharedActor = new SharedActor(actorGroup);
    }


    public Actor createActor(String actorPath) {
        ensureRunning();
        BaseActor actor = new BaseActor(this, actorPath);
        Actor old = actors.putIfAbsent(actorPath, actor);
        if (old != null) {
            throw new IllegalArgumentException("Actor already exists: " + actorPath);
        }
        return actor;
    }

    public Actor getActor(String actorPath) {
        return actors.get(actorPath);
    }

    /**
     * Remove the specified actor
     *
     * @param actorPath actor path
     */
    public void removeActor(String actorPath) {
        Actor removed = actors.remove(actorPath);
        if (removed instanceof BaseActor) {
            ((BaseActor) removed).deactivate();
        }
    }

    /**
     * Get or create actor
     *
     * @param actorPath actor path
     * @return actor
     */
    public Actor getOrCreateActor(String actorPath) {
        ensureRunning();
        return actors.computeIfAbsent(actorPath, path -> new BaseActor(this, path));
    }

    /**
     * Bind shared actor
     *
     * @param key shared actor key
     * @return shared actor
     */
    public Actor bindingSharedActor(long key) {
        return sharedActor.getSharedActor(key);
    }

    @Override
    public void accept(Runnable task) {
        ensureRunning();
        // Do not use submit() here, because submit creates additional FutureTask,
        // which causes unnecessary allocation in high-volume message delivery scenarios.
        // ActorSystem is more suitable for execute (fire-and-forget).
        try {
            threadPool.execute(task);
        } catch (RejectedExecutionException e) {
            throw new IllegalStateException("ActorSystem has been shutdown", e);
        }
    }

    @Override
    public void shutDown() {
        if (running.compareAndSet(true, false)) {
            logger.info("Starting to shutdown ActorSystem...");
            try {
                // Clear actor registry
                actors.clear();

                // Shutdown business executor
                threadPool.shutdown();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }

                logger.info("ActorSystem shutdown completed");
            } catch (Exception e) {
                logger.error("ActorSystem shutdown exception", e);
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

    private void ensureRunning() {
        if (isShutdown()) {
            throw new IllegalStateException("ActorSystem has been shutdown");
        }
    }

    /**
     * Get statistics
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActorSystem Statistics:\n");
        sb.append("Root Queue Current Task Size: ").append(threadPool.getQueue().size()).append("\n");
        sb.append("Root Queue finished Task Size: ").append(threadPool.getCompletedTaskCount()).append("\n");
        sb.append("Shared Queue Workers: ").append(sharedActor.getWorkerSize()).append("\n");

        Actor[] boxGroup = sharedActor.getGroup();
        for (int i = 0; i < boxGroup.length; i++) {
            sb.append("Shared Worker[").append(i).append("]: ").append(boxGroup[i].getMailbox().getTaskSize()).append("\n");
        }
        return sb.toString();
    }
}
