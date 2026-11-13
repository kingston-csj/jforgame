package jforgame.cache;

/**
 * Cache storage abstraction used by {@link BaseEntityCacheService}.
 * The core module ships with {@link LocalCacheStore}; integrations (for example
 * Spring Cache) can provide their own implementation.
 */
public interface CacheStore {

    /**
     * Gets a value from the named cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @return the cached value, or null if absent
     */
    Object get(String cacheName, Object key);

    /**
     * Puts a value into the named cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param value     the value to cache
     */
    void put(String cacheName, Object key, Object value);

    /**
     * Evicts a value from the named cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     */
    void evict(String cacheName, Object key);
}
