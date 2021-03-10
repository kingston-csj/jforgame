package jforgame.common.utils;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jforgame.common.thread.ThreadSafe;

/**
 * LruHashMap is an extension of Java's HashMap, which has a bounded size();
 * When it reaches that size, each time a new element is added, the least recently used (LRU) entry is removed.
 * Java makes it very easy to implement LruHashMap - all its functionality is already available from LinkedHashMap,
 * and we just need to configure that properly.
 * Note that LruHashMap is thread safe
 *
 * @author kinson
 */
@ThreadSafe
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -5167631809472116969L;

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int DEFAULT_MAX_CAPACITY = 1000;
    private final Lock lock = new ReentrantLock();
    private volatile int maxCapacity;

    public LruHashMap() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public LruHashMap(int maxCapacity) {
        super(16, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            lock.lock();
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

}