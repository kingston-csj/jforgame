package jforgame.commons.ds;

import jforgame.commons.thread.ThreadSafe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * A bounded queue cache based on LinkedHashMap implementation.
 * Only checks for expired elements or elements exceeding capacity when adding elements,
 * removes them if conditions are met.
 * When aliveTime parameter is true, it simulates LRU rules.
 */
@ThreadSafe
public class LazyCacheMap<K, V> {

    private LinkedHashMap<K, Element<V>> data;

    /**
     * Maximum cache capacity, when adding new elements, if current length exceeds capacity, head element will be removed
     */
    private int capacity;

    /**
     * Survival duration (milliseconds)
     */
    private long aliveTime;

    /**
     * Whether to use LRU rules, when true means element is moved to tail when accessed
     * {@link LinkedHashMap#accessOrder}}
     */
    private boolean useLru;

    /**
     * Read-write lock
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public LazyCacheMap(int capacity, long aliveTime) {
        this(capacity, aliveTime, Boolean.FALSE);
    }

    public LazyCacheMap(int capacity, long aliveTime, boolean useLru) {
        this.capacity = capacity;
        this.aliveTime = aliveTime;
        this.useLru = useLru;
        this.data = new LinkedHashMap<K, Element<V>>(capacity, 1.0f, useLru) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Element<V>> eldest) {
                if (size() > capacity) {
                    return true;
                }
                long eleAliveTime = System.currentTimeMillis() - eldest.getValue().bornTime;
                return (eleAliveTime > aliveTime);
            }
        };
    }

    public V put(K key, V value) {
        V preValue = null;
        Element<V> newValue = new Element<V>(value);

        writeLock.lock();
        try {
            if (data.containsKey(key)) {
                preValue = data.get(key).value;
            }
            data.put(key, newValue);
            return preValue;
        } finally {
            writeLock.unlock();
        }
    }

    public V get(K key) {
        // When using LRU, element repositioning to tail involves write operations, so use write lock
        Lock lock = this.useLru ? this.writeLock : this.readLock;
        Element<V> target = null;
        lock.lock();
        try {
            target = this.data.get(key);
            if (target == null) {
                return null;
            }
        } finally {
            lock.unlock();
        }
        long eleAlive = System.currentTimeMillis() - target.bornTime;
        if (eleAlive < aliveTime) {
            return target.value;
        }
        removeIfSameElement(key, target);
        return null;
    }

    private void removeIfSameElement(K key, Element<V> expected) {
        this.writeLock.lock();
        try {
            Element<V> current = this.data.get(key);
            if (current == expected) {
                this.data.remove(key);
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    public int size() {
        this.readLock.lock();
        try {
            return this.data.size();
        } finally {
            this.readLock.unlock();
        }
    }

    public void remove(K key) {
        this.writeLock.lock();
        try {
            this.data.remove(key);
        } finally {
            this.writeLock.unlock();
        }
    }

    public void clear() {
        this.writeLock.lock();
        try {
            this.data.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    /**
     * Get all records
     * @return all records
     */
    public List<V> getAllRecords() {
        this.readLock.lock();
        try {
            return data.values().stream().map(e -> e.value).collect(Collectors.toList());
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "LinkedCacheMap [data=" + data + ", capacity=" + capacity + ", aliveTime=" + aliveTime + ", useLru="
                + useLru + "]";
    }

    @SuppressWarnings("hiding")
    static class Element<V> {
        V value;
        /**
         * Timestamp when element was added
         */
        long bornTime;

        Element(V v) {
            this.value = v;
            this.bornTime = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Element [value=" + value + "]";
        }
    }

}