package com.kingston.jforgame.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于LinkedHashMap实现的一个有限队列缓存 有时间及大小的限制
 * aliveTime参数为true时要模拟lru规则
 * 
 * @author kingston
 */
public class LinkedCacheMap<K, V> {

	private LinkedHashMap<K, Element<V>> data;

	/**
	 * 缓存最大容量，添加新元素时，若当前长度超过容量，则会删除队首元素
	 */
	private int capacity;

	/** 生存时长（毫秒数） */
	private long aliveTime;

	/**
	 * 是否使用lru规则，为true时表示当元素被查找命中时被重放到队尾
	 *  {@link LinkedHashMap#accessOrder}}
	 */
	private boolean useLru;

	/** 读写锁 */
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	public LinkedCacheMap(int capacity, long aliveTime) {
		this(capacity, aliveTime, Boolean.FALSE);
	}

	public LinkedCacheMap(int capacity, long aliveTime, boolean useLru) {
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

		readLock.lock();
		try {
			if (data.containsKey(key)) {
				preValue = data.get(key).value;
			}
			data.put(key, newValue);
			return preValue;
		} finally {
			readLock.unlock();
		}
	}

	public V get(K key) {
		// 当使用lru，元素重放回队尾涉及到写操作，所以用写锁
		Lock lock = this.useLru ? this.writeLock : this.readLock;
		lock.lock();
		try {
			Element<V> target = this.data.get(key);
			return target != null ? target.value : null;
		} finally {
			lock.unlock();
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

	@Override
	public String toString() {
		return "LinkedCacheMap [data=" + data + ", capacity=" + capacity + ", aliveTime=" + aliveTime + ", useLru="
				+ useLru + "]";
	}

	@SuppressWarnings("hiding")
	class Element<V> {
		V value;
		/** 元素添加时的时间戳 */
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
	
	
	public static void main(String[] args) throws Exception {
		LinkedCacheMap<String, String> cache = new LinkedCacheMap<>(3, 100, true);
		
		cache.put("a", "a");
		cache.put("b", "b");
		cache.put("c", "c");
		
		cache.get("a");
		cache.put("d", "d");
		
		System.err.println(cache);
		
		cache.clear();
		cache.put("e", "e");
		cache.put("f", "f");
		Thread.sleep(150);
		cache.put("g", "g");
		System.err.println(cache);
	}

}
