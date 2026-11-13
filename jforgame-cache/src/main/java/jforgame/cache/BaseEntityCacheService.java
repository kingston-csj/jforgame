package jforgame.cache;

import jforgame.commons.persist.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default entity cache service implementation.
 * Reads/writes go through the supplied {@link CacheStore} and {@link DatabaseAccessor},
 * so this class does not depend on any dependency injection container.
 *
 * @param <E> the entity type
 */
public class BaseEntityCacheService<E extends Entity>
        implements EntityCacheService<E> {

    private static final Logger logger = LoggerFactory.getLogger(BaseEntityCacheService.class);

    private final Lock[] locks = new Lock[10];

    private final Class<E> entityClass;

    private final DatabaseAccessor databaseAccessor;

    private final CacheStore cacheStore;

    /**
     * Constructor.
     *
     * @param entityClass      the entity class
     * @param databaseAccessor the database accessor
     * @param cacheStore       the cache store
     */
    public BaseEntityCacheService(Class<E> entityClass, DatabaseAccessor databaseAccessor, CacheStore cacheStore) {
        this.entityClass = entityClass;
        this.databaseAccessor = databaseAccessor;
        this.cacheStore = cacheStore;
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    /**
     * Gets the cache name for the entity.
     * Default implementation uses the entity class simple name.
     *
     * @return the cache name
     */
    protected String getCacheName() {
        return entityClass.getSimpleName();
    }

    /**
     * Gets an entity by ID from cache or database.
     * If not in cache, loads from database using {@link DatabaseAccessor}.
     *
     * @param id the entity ID
     * @return the entity, or null if not found
     */
    @Override
    @SuppressWarnings("unchecked")
    public E getEntity(Object id) {
        Object cached = cacheStore.get(getCacheName(), id);
        if (cached != null) {
            return (E) cached;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("type=cacheLoad,cache={},id={}", getCacheName(), id);
        }
        E entity = databaseAccessor.load(entityClass, id);
        if (entity != null) {
            cacheStore.put(getCacheName(), id, entity);
        }
        return entity;
    }

    /**
     * Saves an entity to database and updates the cache.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    @Override
    public E putEntity(E entity) {
        if (logger.isDebugEnabled()) {
            logger.debug("type=cacheSave,cache={},id={}", getCacheName(), entity.getId());
        }
        databaseAccessor.saveOrUpdate(entity);
        cacheStore.put(getCacheName(), entity.getId(), entity);
        return entity;
    }

    /**
     * Removes an entity from database and cache.
     *
     * @param entity the entity to remove
     */
    @Override
    public void removeEntity(E entity) {
        databaseAccessor.remove(entity);
        cacheStore.evict(getCacheName(), entity.getId());
    }

    /**
     * Gets or creates an entity with double-checked locking.
     * If the entity doesn't exist, creates it using the provided builder.
     *
     * @param id      the entity ID
     * @param builder the entity builder
     * @return the existing or newly created entity
     */
    public E getOrCreate(Object id, EntityBuilder<E> builder) {
        E entity = getEntity(id);
        if (entity != null) {
            return entity;
        }
        int index = Math.abs(id.hashCode()) % locks.length;
        Lock lock = locks[index];
        lock.lock();
        try {
            entity = getEntity(id);
            if (entity == null) {
                entity = builder.build();
                putEntity(entity);
            }
        } finally {
            lock.unlock();
        }
        return entity;
    }

    /**
     * Gets the entity class handled by this service.
     *
     * @return the entity class
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

}
