package jforgame.commons.persist.strategy;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.container.PersistContainerGroup;

/**
 * Sharding strategy for persist container group
 * <p>
 * Decide which sub-container an entity should be routed to,
 * similar to database table partitioning strategy.
 * <p>
 * Custom implementations can provide consistency hashing,
 * round-robin, business-key based routing, etc.
 *
 * @see HashShardingStrategy
 * @see PersistContainerGroup
 */
public interface ShardingStrategy {

    /**
     * Compute the target sub-container index for the given entity
     *
     * @param entity      entity to be persisted
     * @param workerCount sub-container count
     * @return target index, range [0, workerCount)
     */
    int shard(Entity<?> entity, int workerCount);

}
