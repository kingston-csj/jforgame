package jforgame.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link CacheStore} implementation backed by {@link ConcurrentHashMap}.
 * Used when no external cache (such as Spring Cache) is configured.
 */
public final class LocalCacheStore implements CacheStore {

    private final Map<String, Map<Object, Object>> caches = new ConcurrentHashMap<>();

    @Override
    public Object get(String cacheName, Object key) {
        Map<Object, Object> cache = caches.get(cacheName);
        return cache == null ? null : cache.get(key);
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        caches.computeIfAbsent(cacheName, name -> new ConcurrentHashMap<>()).put(key, value);
    }

    @Override
    public void evict(String cacheName, Object key) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.remove(key);
        }
    }
}
