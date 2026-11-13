package jforgame.cache;

import jforgame.commons.persist.Entity;

/**
 * Factory interface for creating entity cache service instances.
 */
public interface EntityCacheServiceFactory {

    /**
     * Creates an entity cache service for the given entity class.
     *
     * @param entityClass the entity class
     * @param <E>         the entity type
     * @return the cache service instance
     */
    <E extends Entity> BaseEntityCacheService<E> create(Class<E> entityClass);
}
