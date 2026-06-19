package jforgame.commons.ds;

import jforgame.commons.thread.ThreadSafe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LruHashMap is an extension class of LinkedHashMap in Java, with capacity limit feature;
 * When capacity reaches the limit, each time adding a new element, it will automatically remove the Least Recently Used (LRU) entry.
 * Implementing LruHashMap in Java is very simple - all core functions needed are provided by LinkedHashMap,
 * we just need to configure LinkedHashMap properly to achieve LruHashMap functionality.
 * Note: This class is thread-safe.
 * Since LinkedHashMap constructor when accessOrder=true, will automatically move recently accessed elements to the tail of the linked list, involving write operations, therefore most operations of this class are actually serial, with lower efficiency.
 */
@ThreadSafe
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_MAX_CAPACITY = 1000;
    // Minimum capacity set to 8, balancing practicality and hash table implementation characteristics
    private static final int MIN_MAX_CAPACITY = 8;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private volatile int maxCapacity;

    public LruHashMap() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public LruHashMap(int maxCapacity) {
        super(16, DEFAULT_LOAD_FACTOR, true);
        // Validate minimum value during initialization
        this.maxCapacity = Math.max(maxCapacity, MIN_MAX_CAPACITY);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.maxCapacity;
    }

    @Override
    public boolean containsKey(Object key) {
        readLock.lock();
        try {
            return super.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        writeLock.lock();
        try {
            return super.get(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        writeLock.lock();
        try {
            return super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        writeLock.lock();
        try {
            return super.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return super.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            super.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public void setMaxCapacity(int maxCapacity) {
        writeLock.lock();
        try {
            // Validate minimum value when setting
            this.maxCapacity = Math.max(maxCapacity, MIN_MAX_CAPACITY);
            // Ensure capacity does not exceed newly set maximum value
            while (size() > this.maxCapacity) {
                Map.Entry<K, V> eldest = entrySet().iterator().next();
                remove(eldest.getKey());
            }
        } finally {
            writeLock.unlock();
        }
    }

    public int getMaxCapacity() {
        readLock.lock();
        try {
            return this.maxCapacity;
        } finally {
            readLock.unlock();
        }
    }

}