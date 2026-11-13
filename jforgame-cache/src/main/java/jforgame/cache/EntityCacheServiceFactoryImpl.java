package jforgame.cache;

import jforgame.commons.persist.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default {@link EntityCacheServiceFactory} implementation.
 * Creates {@link BaseEntityCacheService} instances and caches them by entity class.
 */
public class EntityCacheServiceFactoryImpl implements EntityCacheServiceFactory {

    private final Map<Class<?>, BaseEntityCacheService<?>> cache = new ConcurrentHashMap<>();

    private final DatabaseAccessor databaseAccessor;

    private final CacheStore cacheStore;

    /**
     * Constructor.
     *
     * @param databaseAccessor the database accessor shared by all cache services
     * @param cacheStore       the cache store shared by all cache services
     */
    public EntityCacheServiceFactoryImpl(DatabaseAccessor databaseAccessor, CacheStore cacheStore) {
        this.databaseAccessor = databaseAccessor;
        this.cacheStore = cacheStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> BaseEntityCacheService<E> create(Class<E> entityClass) {
        BaseEntityCacheService<?> service = cache.computeIfAbsent(entityClass,
                clazz -> new BaseEntityCacheService<>(entityClass, databaseAccessor, cacheStore));
        return (BaseEntityCacheService<E>) service;
    }
}
