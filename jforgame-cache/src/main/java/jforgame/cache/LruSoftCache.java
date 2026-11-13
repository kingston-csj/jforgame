package jforgame.cache;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentMap;

/**
 * A two-level cache backed by a hard LRU cache and a soft-reference cache.
 *
 * <p>Values are stored in {@code hardCache} for fast access. When the hard cache
 * reaches its capacity and evicts an entry, the evicted value is wrapped in a
 * {@link SoftReference} and placed into {@code softCache}. This allows recently
 * evicted entries to be recovered if they have not yet been garbage collected.
 *
 * <p>The soft cache is also bounded: it holds at most twice the hard cache capacity,
 * so cleared references cannot accumulate without limit. When it overflows, its least
 * recently used entries are discarded.
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
     * The primary cache with a bounded LRU eviction policy.
     */
    private final ConcurrentMap<K, V> hardCache;

    /**
     * The secondary cache that holds soft references to evicted entries.
     * Entries in this map may be cleared by the garbage collector under memory pressure,
     * and the map itself is bounded to twice the hard cache capacity.
     */
    private final ConcurrentMap<K, SoftReference<V>> softCache;

    /**
     * Constructs a new {@code LruSoftCache} with the specified maximum capacity.
     *
     * @param maxSize the maximum number of entries the hard cache can hold
     */
    public LruSoftCache(int maxSize) {
        this.softCache = new ConcurrentLinkedHashMap.Builder<K, SoftReference<V>>()
                .maximumWeightedCapacity((long) maxSize * SOFT_CAPACITY_FACTOR)
                .weigher(Weighers.singleton())
                .build();
        this.hardCache = new ConcurrentLinkedHashMap.Builder<K, V>()
                .maximumWeightedCapacity(maxSize)
                .weigher(Weighers.singleton())
                // When an entry is evicted from the hard cache, move it into the soft cache.
                .listener((key, value) -> softCache.put(key, new SoftReference<>(value)))
                .build();
    }

    /**
     * Associates the specified value with the specified key in this cache.
     *
     * <p>If the cache previously contained a mapping for the key, the old value
     * is replaced and returned. The corresponding soft reference is removed so
     * that the hard cache remains the authoritative source.
     *
     * @param k the key with which the specified value is to be associated
     * @param v the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     */
    public V put(K k, V v) {
        if (v == null) {
            return null;
        }
        V previous = hardCache.put(k, v);
        softCache.remove(k);
        return previous;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this cache contains no mapping for the key.
     *
     * <p>If the hard cache does not contain the key, the soft cache is consulted.
     * When a live value is found in the soft cache, it is promoted back into the
     * hard cache so that subsequent accesses can be served directly from it.
     *
     * @param k the key whose associated value is to be returned
     * @return the value associated with the key, or {@code null} if not found
     */
    public V get(K k) {
        V value = hardCache.get(k);
        if (value != null) {
            return value;
        }

        SoftReference<V> ref = softCache.get(k);
        if (ref == null) {
            return null;
        }

        value = ref.get();
        if (value == null) {
            // The softly referenced value has been garbage collected.
            softCache.remove(k);
            return null;
        }

        // Promote the recovered value back into the hard cache and drop the soft entry.
        softCache.remove(k);
        hardCache.put(k, value);
        return value;
    }

    /**
     * Removes the mapping for the specified key from both the hard and soft caches.
     *
     * @param k the key whose mapping is to be removed
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     */
    public V remove(K k) {
        softCache.remove(k);
        return hardCache.remove(k);
    }

    /**
     * Removes all mappings from both the hard and soft caches.
     */
    public void clear() {
        softCache.clear();
        hardCache.clear();
    }
}