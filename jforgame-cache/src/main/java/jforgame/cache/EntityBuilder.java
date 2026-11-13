package jforgame.cache;

/**
 * Functional interface for building entity instances.
 * Used in {@link BaseEntityCacheService#getOrCreate(Object, EntityBuilder)} to create new entities.
 *
 * @param <E> the entity type
 */
@FunctionalInterface
public interface EntityBuilder<E> {

    /**
     * Builds a new entity instance.
     * The business layer is responsible for initializing the entity.
     *
     * @return the new entity instance
     */
    E build();

}
