package jforgame.cache.spring;

import jforgame.cache.CacheStore;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * {@link CacheStore} implementation that delegates to the Spring {@link CacheManager},
 * so entity caches can share the same cache provider (ConcurrentMap, Redis, etc.).
 */
public final class SpringCacheStore implements CacheStore {

    private final CacheManager cacheManager;

    public SpringCacheStore(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Object get(String cacheName, Object key) {
        Cache.ValueWrapper wrapper = cache(cacheName).get(key);
        return wrapper == null ? null : wrapper.get();
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        cache(cacheName).put(key, value);
    }

    @Override
    public void evict(String cacheName, Object key) {
        cache(cacheName).evict(key);
    }

    private Cache cache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("No cache named '" + cacheName + "' in CacheManager");
        }
        return cache;
    }
}
