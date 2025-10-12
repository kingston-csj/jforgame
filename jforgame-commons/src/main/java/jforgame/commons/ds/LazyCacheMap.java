package jforgame.commons.ds;

import jforgame.commons.thread.ThreadSafe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 基于LinkedHashMap实现的一个有限队列缓存
 * 只当在添加元素的时候会检测超时元素或者超出容量的元素，满足条件则将其移除
 * aliveTime参数为true时为模拟lru规则
 */
@ThreadSafe
public class LazyCacheMap<K, V> {

    private LinkedHashMap<K, Element<V>> data;

    /**
     * 缓存最大容量，添加新元素时，若当前长度超过容量，则会删除队首元素
     */
    private int capacity;

    /**
     * 生存时长（毫秒数）
     */
    private long aliveTime;

    /**
     * 是否使用lru规则，为true时表示当元素被查找命中时被重放到队尾
     * {@link LinkedHashMap#accessOrder}}
     */
    private boolean useLru;

    /**
     * 读写锁
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
        // 当使用lru，元素重放回队尾涉及到写操作，所以用写锁
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
        remove(key);
        return null;
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
     * 获取所有的记录
     * @return 所有记录
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
         * 元素添加时的时间戳
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