package jforgame.commons.ds;

import jforgame.commons.thread.ThreadSafe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LruHashMap 是 Java 中 LinkedHashMap 的扩展类，具有容量限制特性；
 * 当容量达到上限后，每次添加新元素时，会自动移除最近最少使用（LRU，Least Recently Used） 的条目。
 * 在 Java 中实现 LruHashMap 非常简便 —— 其所需的全部核心功能已由 LinkedHashMap 提供，
 * 我们只需对 LinkedHashMap 进行适当配置即可实现 LruHashMap 的功能。
 * 注意：本类是线程安全的。
 */
@ThreadSafe
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_MAX_CAPACITY = 1000;
    // 最小容量设置为8，兼顾实用性和哈希表实现特点
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
        // 初始化时校验最小值
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
        readLock.lock();
        try {
            return super.get(key);
        } finally {
            readLock.unlock();
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
            // 设置时校验最小值
            this.maxCapacity = Math.max(maxCapacity, MIN_MAX_CAPACITY);
            // 确保容量不超过新设置的最大值
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