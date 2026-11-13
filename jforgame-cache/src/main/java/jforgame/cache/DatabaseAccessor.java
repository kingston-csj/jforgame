package jforgame.cache;

import jforgame.commons.persist.Entity;

/**
 * Database accessor interface that abstracts database operations.
 * Implemented by the business layer to provide database access for the framework layer.
 */
public interface DatabaseAccessor {

    /**
     * Queries a single entity by ID.
     *
     * @param entityClass the entity class
     * @param id          the entity ID
     * @param <E>         the entity type
     * @return the entity, or null if not found
     */
    <E> E load(Class<E> entityClass, Object id);

    /**
     * Saves an entity to the database.
     *
     * @param entity the entity to save
     */
    void saveOrUpdate(Entity<?> entity);

    /**
     * Deletes an entity from cache and the database.
     *
     * @param entity the entity to delete
     */
    void remove(Entity<?> entity);
}
