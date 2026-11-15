package jforgame.demo.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jforgame.cache.CacheStore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Guava Cache 的命名缓存实现，每个实体类型使用独立的缓存。
 */
public final class GuavaCacheStore implements CacheStore {

    private final CacheOptions options;

    private final ConcurrentMap<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();

    public GuavaCacheStore() {
        this(CacheOptions.defaultCacheOptions());
    }

    public GuavaCacheStore(CacheOptions options) {
        this.options = options;
    }

    @Override
    public Object get(String cacheName, Object key) {
        Cache<Object, Object> cache = caches.get(cacheName);
        return cache == null ? null : cache.getIfPresent(key);
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        caches.computeIfAbsent(cacheName, name -> createCache()).put(key, value);
    }

    @Override
    public void evict(String cacheName, Object key) {
        Cache<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.invalidate(key);
        }
    }

    private Cache<Object, Object> createCache() {
        return CacheBuilder.newBuilder()
                .initialCapacity(options.initialCapacity)
                .maximumSize(options.maximumSize)
                .expireAfterAccess(options.expireAfterAccessSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(options.expireAfterWriteSeconds, TimeUnit.SECONDS)
                .build();
    }
}
