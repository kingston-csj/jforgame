package jforgame.commons.persist.container;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.HashShardingStrategy;
import jforgame.commons.persist.strategy.ShardingStrategy;

import java.util.Arrays;

/**
 * Unified persist container group
 * <p>
 * Combine several {@link PersistContainer}s into a group, route entity to sub-container
 * by {@link ShardingStrategy}. Sub-containers can be of any type (Queue, Delay, Cron,
 * or even mixed types), which removes the need to write a dedicated group class for each
 * container type.
 * <p>
 * Usage examples:
 * <pre>{@code
 * // 1. QueueContainer group (replaces old QueueContainerGroup)
 * PersistContainer[] queues = new PersistContainer[workers];
 * for (int i = 0; i < workers; i++) {
 *     queues[i] = new QueueContainer(name, savingStrategy);
 * }
 * PersistContainerGroup group = new PersistContainerGroup(name, queues);
 *
 * // 2. DelayContainer group (replaces old DelayContainerGroup)
 * PersistContainer[] delays = new PersistContainer[workers];
 * for (int i = 0; i < workers; i++) {
 *     delays[i] = new DelayContainer(name, delaySeconds, savingStrategy);
 * }
 * PersistContainerGroup group = new PersistContainerGroup(name, delays);
 *
 * // 3. Custom sharding strategy
 * PersistContainerGroup group = new PersistContainerGroup(name, containers, new ConsistentHashShardingStrategy());
 * }</pre>
 *
 * @see HashShardingStrategy
 */
public final class PersistContainerGroup extends BasePersistContainer {

    /**
     * Sub-container array
     */
    private final PersistContainer[] group;

    /**
     * Sharding strategy, decide which sub-container an entity is routed to
     */
    private final ShardingStrategy shardingStrategy;

    /**
     * Build a group with the given sub-containers, use default {@link HashShardingStrategy}
     *
     * @param name  group name
     * @param group sub-container array, must not be empty
     */
    public PersistContainerGroup(String name, PersistContainer[] group) {
        this(name, group, new HashShardingStrategy());
    }

    /**
     * Build a group with the given sub-containers and sharding strategy
     *
     * @param name             group name
     * @param group            sub-container array, must not be empty
     * @param shardingStrategy sharding strategy
     */
    public PersistContainerGroup(String name, PersistContainer[] group, ShardingStrategy shardingStrategy) {
        if (group == null || group.length == 0) {
            throw new IllegalArgumentException("sub-container array must not be empty");
        }
        if (shardingStrategy == null) {
            throw new IllegalArgumentException("shardingStrategy must not be null");
        }
        this.name = name;
        this.group = group;
        this.shardingStrategy = shardingStrategy;
    }

    @Override
    public void receive(Entity<?> entity) {
        if (!run.get()) {
            logger.info("db closed, received entity key: {}", entity.getKey());
            return;
        }
        int index = shardingStrategy.shard(entity, group.length);
        group[index].receive(entity);
    }

    @Override
    public int size() {
        int size = 0;
        for (PersistContainer container : group) {
            size += container.size();
        }
        return size;
    }

    /**
     * Gracefully shutdown every sub-container, ensure all cached data is flushed
     */
    @Override
    protected void saveAllBeforeShutdown() {
        for (PersistContainer container : group) {
            container.shutdownGraceful();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "PersistContainerGroup{groupName='%s', workerCount=%d, shardingStrategy=%s, children=%s}",
                this.name,
                group.length,
                shardingStrategy.getClass().getSimpleName(),
                Arrays.toString(group)
        );
    }
}
