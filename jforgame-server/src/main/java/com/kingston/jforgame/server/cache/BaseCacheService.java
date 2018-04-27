package com.kingston.jforgame.server.cache;

/**
 * 抽象缓存服务
 * @author kingston
 */
public abstract class BaseCacheService<K, V> implements Persistable<K, V> {

    private final AbstractCacheContainer<K, V> container;

    public BaseCacheService() {
        this(CacheOptions.defaultCacheOptions());
    }

    public BaseCacheService(CacheOptions p) {
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
