package jforgame.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A {@link CacheStore} implementation that combines a bounded LRU hard cache with a
 * soft-reference overflow cache for every named cache.
 *
 * <p>Each cache name owns an independent {@link LruSoftCache}. The hard cache is limited
 * to {@code hardMaxSize} entries per cache name. When an entry is evicted from the hard
 * cache it is kept as a {@link java.lang.ref.SoftReference} in the soft cache, so it can
 * be reclaimed by the garbage collector under memory pressure and recovered on a later
 * access while it is still alive. The soft cache is bounded to twice {@code hardMaxSize}.
 *
 * <p>This class requires {@code concurrentlinkedhashmap-lru} on the runtime classpath;
 * the dependency is declared as {@code provided} by the module.
 */
public final class LruSoftCacheStore implements CacheStore {

    /**
     * Default maximum number of hard-cache entries for each named cache.
     */
    private static final int DEFAULT_HARD_MAX_SIZE = 1024;

    /**
     * Maximum number of hard-cache entries for each named cache.
     */
    private final int hardMaxSize;

    /**
     * Named caches. A cache is created lazily on the first write to that cache name.
     */
    private final ConcurrentMap<String, LruSoftCache<Object, Object>> caches = new ConcurrentHashMap<>();

    /**
     * Creates a store where every named cache holds at most
     * {@value #DEFAULT_HARD_MAX_SIZE} entries in its hard cache.
     */
    public LruSoftCacheStore() {
        this(DEFAULT_HARD_MAX_SIZE);
    }

    /**
     * Creates a store with the given hard-cache capacity for each named cache.
     *
     * @param hardMaxSize the maximum number of hard-cache entries per cache name, must be positive
     * @throws IllegalArgumentException if {@code hardMaxSize} is not positive
     */
    public LruSoftCacheStore(int hardMaxSize) {
        if (hardMaxSize <= 0) {
            throw new IllegalArgumentException("Params must be positive");
        }
        this.hardMaxSize = hardMaxSize;
    }

    /**
     * Returns the value associated with the given key.
     *
     * <p>The hard cache is consulted first. If the key was previously evicted, the soft
     * cache is checked and a still-live value is promoted back into the hard cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @return the cached value, or {@code null} if the cache does not exist or the entry
     *         is absent / has been garbage collected
     */
    @Override
    public Object get(String cacheName, Object key) {
        LruSoftCache<Object, Object> cache = caches.get(cacheName);
        return cache == null ? null : cache.get(key);
    }

    /**
     * Stores a value in the named cache, creating the cache on first use.
     *
     * <p>{@code null} values are ignored and never cached. If the hard cache exceeds its
     * capacity, the least recently used entry is moved to the soft cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param value     the value to cache; {@code null} is ignored
     */
    @Override
    public void put(String cacheName, Object key, Object value) {
        if (value == null) return;
        caches.computeIfAbsent(cacheName, name -> new LruSoftCache<>(hardMaxSize))
                .put(key, value);
    }

    /**
     * Removes the entry for the given key from both the hard cache and the soft cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     */
    @Override
    public void evict(String cacheName, Object key) {
        LruSoftCache<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.remove(key);
        }
    }

}
