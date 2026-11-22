package jforgame.cache;

import jforgame.commons.util.TimeUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A multi-cache store that manages multiple named {@link LruSoftCache} instances.
 *
 * <p>Each cache name maps to an independent LRU + soft-reference cache with the
 * same capacity, TTL and cleanup configuration. Caches are created lazily on the
 * first write.
 *
 * <p>Expired entries are removed lazily during write operations. For each named cache,
 * a periodic cleanup is triggered only after a configurable number of {@code put}
 * calls (default 100). When triggered, the entire hard cache is scanned and entries
 * whose last access time exceeds the configured TTL are evicted. Reads do not trigger
 * cleanup, so idle entries may remain visible until the next write cycle.
 */
public final class LruSoftCacheStore implements CacheStore {

    /**
     * Default maximum number of hard-cache entries for each named cache.
     */
    private static final int DEFAULT_HARD_MAX_SIZE = 1024;

    /**
     * Default number of put operations between cleanup runs.
     */
    private static final long DEFAULT_CLEANUP_INTERVAL = 100;

    /**
     * Default time-to-live in milliseconds.
     */
    private static final long DEFAULT_CLEANUP_TTL = TimeUtil.MILLIS_PER_HOUR;


    /**
     * Maximum number of hard-cache entries for each named cache.
     */
    private final int hardMaxSize;

    /**
     * Time-to-live in milliseconds based on last access time.
     */
    private final long ttlMillis;

    /**
     * Number of put operations between cleanup runs for each named cache.
     */
    private final long cleanupInterval;

    /**
     * Named caches. A cache is created lazily on the first write to that cache name.
     */
    private final ConcurrentMap<String, LruSoftCache<Object, Object>> caches = new ConcurrentHashMap<>();

    /**
     * Creates a store where every named cache holds at most
     * {@value #DEFAULT_HARD_MAX_SIZE} entries in its hard cache and never expires.
     */
    public LruSoftCacheStore() {
        this(DEFAULT_HARD_MAX_SIZE, DEFAULT_CLEANUP_TTL, DEFAULT_CLEANUP_INTERVAL);
    }

    /**
     * Creates a store with the given hard-cache capacity and no expiration.
     *
     * @param hardMaxSize the maximum number of hard-cache entries per cache name, must be positive
     * @throws IllegalArgumentException if {@code hardMaxSize} is not positive
     */
    public LruSoftCacheStore(int hardMaxSize) {
        this(hardMaxSize, DEFAULT_CLEANUP_TTL, DEFAULT_CLEANUP_INTERVAL);
    }

    /**
     * Creates a store with the given hard-cache capacity, TTL and cleanup interval.
     *
     * @param hardMaxSize     the maximum number of hard-cache entries per cache name, must be positive
     * @param ttlMillis       the maximum idle time in milliseconds before an entry is considered expired;
     *                        zero or negative means no expiration
     * @param cleanupInterval the number of put operations between cleanup runs;
     *                        if not positive, {@value #DEFAULT_CLEANUP_INTERVAL} is used
     * @throws IllegalArgumentException if {@code hardMaxSize} is not positive
     */
    public LruSoftCacheStore(int hardMaxSize, long ttlMillis, long cleanupInterval) {
        if (hardMaxSize <= 0) {
            throw new IllegalArgumentException("Params must be positive");
        }
        this.hardMaxSize = hardMaxSize;
        this.ttlMillis = ttlMillis;
        this.cleanupInterval = cleanupInterval > 0 ? cleanupInterval : DEFAULT_CLEANUP_INTERVAL;
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
     * is absent / has been garbage collected
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
     * capacity, the least recently used entry is moved to the soft cache. Expired entries
     * are cleaned up periodically based on the configured cleanup interval.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param value     the value to cache; {@code null} is ignored
     */
    @Override
    public void put(String cacheName, Object key, Object value) {
        if (value == null) {
            return;
        }
        caches.computeIfAbsent(cacheName, name -> new LruSoftCache<>(hardMaxSize, ttlMillis, cleanupInterval))
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