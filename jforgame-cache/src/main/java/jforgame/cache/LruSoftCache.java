package jforgame.cache;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A two-level local cache that combines a bounded LRU hard cache with a soft-reference
 * secondary cache. Entries evicted from the hard cache are retained in the soft cache
 * until they are either promoted back or garbage collected.
 *
 * <p>This implementation supports time-based eviction based on the last access time.
 * If an entry has not been read or written within the configured TTL, it is removed
 * during periodic cleanup triggered by write operations.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
class LruSoftCache<K, V> {

    /**
     * The soft cache capacity is twice the hard cache capacity.
     */
    private static final int SOFT_CAPACITY_FACTOR = 2;

    /**
     * Default number of put operations between cleanup runs.
     */
    private static final long DEFAULT_CLEANUP_INTERVAL = 100;

    /**
     * Time-to-live in milliseconds based on last access time.
     * A value of zero or negative means entries never expire.
     */
    private final long ttlMillis;

    /**
     * How many put operations must occur before the next cleanup is triggered.
     */
    private final long cleanupInterval;

    /**
     * Monotonically increasing counter tracking put operations since the last cleanup.
     */
    private final AtomicLong putCounter = new AtomicLong(0);

    /**
     * The primary cache with a bounded LRU eviction policy.
     */
    private final ConcurrentMap<K, CacheEntry<V>> hardCache;

    /**
     * The secondary cache that holds soft references to evicted entries.
     */
    private final ConcurrentMap<K, SoftReference<CacheEntry<V>>> softCache;

    /**
     * Constructs a new {@code LruSoftCache} with the specified maximum capacity
     * and time-to-live. Cleanup runs every 100 put operations.
     *
     * @param maxSize   the maximum number of entries the hard cache can hold
     * @param ttlMillis the maximum idle time in milliseconds before an entry is
     *                  considered expired; zero or negative means no expiration
     */
    public LruSoftCache(int maxSize, long ttlMillis) {
        this(maxSize, ttlMillis, DEFAULT_CLEANUP_INTERVAL);
    }

    /**
     * Constructs a new {@code LruSoftCache} with full control over cleanup frequency.
     *
     * @param maxSize         the maximum number of entries the hard cache can hold
     * @param ttlMillis       the maximum idle time in milliseconds before an entry is
     *                        considered expired; zero or negative means no expiration
     * @param cleanupInterval the number of put operations between cleanup runs
     */
    public LruSoftCache(int maxSize, long ttlMillis, long cleanupInterval) {
        this.ttlMillis = ttlMillis;
        this.cleanupInterval = cleanupInterval > 0 ? cleanupInterval : DEFAULT_CLEANUP_INTERVAL;

        this.softCache = new ConcurrentLinkedHashMap.Builder<K, SoftReference<CacheEntry<V>>>()
                .maximumWeightedCapacity((long) maxSize * SOFT_CAPACITY_FACTOR)
                .weigher(Weighers.singleton())
                .build();

        this.hardCache = new ConcurrentLinkedHashMap.Builder<K, CacheEntry<V>>()
                .maximumWeightedCapacity(maxSize)
                .weigher(Weighers.singleton())
                .listener((key, entry) -> softCache.put(key, new SoftReference<>(entry)))
                .build();
    }

    /**
     * Associates the specified value with the specified key in this cache.
     *
     * <p>If the cache previously contained a mapping for the key, the old value
     * is replaced and returned. The corresponding soft reference is removed so
     * that the hard cache remains the authoritative source.
     *
     * <p>Expired entries are cleaned up only when the put counter reaches the
     * configured interval, avoiding the cost of a full traversal on every write.
     *
     * @param k the key with which the specified value is to be associated
     * @param v the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     */
    public V put(K k, V v) {
        if (v == null) {
            return null;
        }

        if (ttlMillis > 0 && putCounter.incrementAndGet() >= cleanupInterval) {
            putCounter.set(0);
            cleanUpExpiredEntries();
        }

        CacheEntry<V> newEntry = new CacheEntry<>(v);
        CacheEntry<V> oldEntry = hardCache.put(k, newEntry);
        softCache.remove(k);

        return (oldEntry != null) ? oldEntry.value : null;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this cache contains no mapping for the key.
     *
     * <p>This method does not enforce expiration. If the entry exists in the hard
     * cache, it is returned regardless of whether it has exceeded the TTL. The last
     * access time is updated so that actively used entries remain alive.
     *
     * <p>If the hard cache does not contain the key, the soft cache is consulted.
     * When a live value is found in the soft cache, it is promoted back into the
     * hard cache so that subsequent accesses can be served directly from it.
     *
     * @param k the key whose associated value is to be returned
     * @return the value associated with the key, or {@code null} if not found
     */
    public V get(K k) {
        CacheEntry<V> entry = hardCache.get(k);
        if (entry != null) {
            entry.lastAccessTime = System.currentTimeMillis();
            return entry.value;
        }

        SoftReference<CacheEntry<V>> ref = softCache.get(k);
        if (ref == null) {
            return null;
        }

        CacheEntry<V> softEntry = ref.get();
        if (softEntry == null) {
            softCache.remove(k);
            return null;
        }

        softEntry.lastAccessTime = System.currentTimeMillis();
        softCache.remove(k);
        hardCache.put(k, softEntry);

        return softEntry.value;
    }

    /**
     * Removes the mapping for the specified key from both the hard and soft caches.
     *
     * @param k the key whose mapping is to be removed
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     */
    public V remove(K k) {
        softCache.remove(k);
        CacheEntry<V> entry = hardCache.remove(k);
        return (entry != null) ? entry.value : null;
    }

    /**
     * Removes all mappings from both the hard and soft caches.
     */
    public void clear() {
        softCache.clear();
        hardCache.clear();
        putCounter.set(0);
    }

    /**
     * Removes entries from the hard cache whose last access time exceeds the
     * configured TTL.
     *
     * <p>This method is invoked periodically during write operations, not on
     * every put, to amortize the cost of iterating over the entire hard cache.
     */
    private void cleanUpExpiredEntries() {
        long now = System.currentTimeMillis();
        hardCache.entrySet().removeIf(entry -> (now - entry.getValue().lastAccessTime) > ttlMillis);
    }

    /**
     * A cache entry that wraps the stored value and tracks the last access time.
     *
     * @param <V> the type of the cached value
     */
    private static class CacheEntry<V> {

        /**
         * The cached value.
         */
        final V value;

        /**
         * The last time this entry was read or written, in milliseconds since the epoch.
         */
        volatile long lastAccessTime;

        /**
         * Constructs a new cache entry with the given value.
         *
         * @param value the cached value
         */
        CacheEntry(V value) {
            this.value = value;
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
}