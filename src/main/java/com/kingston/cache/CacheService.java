package com.kingston.cache;

/**
 * 抽象缓存服务
 * @author kingston
 */
public abstract class CacheService<K, V> implements Persistable<K, V> {

    private final CacheContainer<K, V> container;

    public CacheService() {
        this(CacheOptions.defaultCacheOptions());
    }

    public CacheService(CacheOptions p) {
        container = new DefaultCacheContainer<>(this, p);
    }

    /**
     * 通过key获取对象
     * @param key
     * @return
     */
    public V get(K key) {
        return container.get(key);
    }

    /**
     * 手动移除缓存
     * @param key
     * @return
     */
    public void remove(K key) {
        container.remove(key);
    }

    /**
     * 手动加入缓存
     * @param key
     * @return
     */
    public void put(K key, V v)  {
        this.container.put(key, v);
    }
    
}
