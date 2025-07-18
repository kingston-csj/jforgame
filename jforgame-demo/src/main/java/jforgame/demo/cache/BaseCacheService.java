package jforgame.demo.cache;

import java.util.concurrent.Callable;

import jforgame.orm.entity.BaseEntity;
import jforgame.demo.db.AsyncDbService;

/**
 * 抽象缓存服务
 */
public abstract class BaseCacheService<K, V extends BaseEntity> implements Persistable<K, V> {

    private final AbstractCacheContainer<K, V> container;

    public BaseCacheService() {
        this(CacheOptions.defaultCacheOptions());
    }

    public BaseCacheService(CacheOptions p) {
        container = new DefaultCacheContainer<>(this, p);
    }

    /**
     * 通过key获取对象
     *
     * @param key
     * @return
     */
    public V get(K key) {
        return container.get(key);
    }

    public final V getOrCreate(K k, Callable<V> callable) {
        return container.getOrCreate(k, callable);
    }

    /**
     * 手动移除缓存
     *
     * @param key
     * @return
     */
    public void remove(K key) {
        container.remove(key);
    }

    /**
     * 手动加入缓存
     *
     * @param key
     * @return
     */
    public void put(K key, V v) {
        this.container.put(key, v);
    }

    public void save(V v) {
        AsyncDbService.getInstance().saveToDb(v);
    }

}
