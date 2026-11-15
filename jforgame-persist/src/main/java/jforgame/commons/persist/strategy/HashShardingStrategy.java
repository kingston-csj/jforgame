package jforgame.commons.persist.strategy;

import jforgame.commons.persist.Entity;

/**
 * Default hash-based sharding strategy
 * <p>
 * Use {@code Math.abs(entity.getId().hashCode()) % workerCount} to route entity,
 * ensures the same entity is always routed to the same sub-container,
 * which keeps per-key persistence order on a single worker thread.
 */
public class HashShardingStrategy implements ShardingStrategy {

    @Override
    public int shard(Entity<?> entity, int workerCount) {
        return Math.abs(entity.getId().hashCode()) % workerCount;
    }

}
