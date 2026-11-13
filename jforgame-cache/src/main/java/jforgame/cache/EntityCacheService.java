package jforgame.cache;


import jforgame.commons.persist.Entity;

/**
 * Interface for entity cache services.
 * Provides methods for getting, saving, and removing entities with caching support.
 *
 * @param <E> the entity type
 */
public interface EntityCacheService<E extends Entity> {


    /**
     * Gets an entity by ID.
     *
     * @param id the entity ID
     * @return the entity, or null if not found
     */
    E getEntity(Object id);

    /**
     * Saves or updates an entity.
     * If the entity already exists in the database, performs an update; otherwise, performs an insert.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    E putEntity(E entity);

    /**
     * Removes an entity.
     *
     * @param entity the entity to remove
     */
    default void removeEntity(E entity) {
        throw new UnsupportedOperationException();
    }
}
