package com.kingston.jforgame.server.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 缓存容器
 * @author kingston
 */
public abstract class AbstractCacheContainer<K, V> {

    private LoadingCache<K, V> cache;

    public AbstractCacheContainer(CacheOptions p) {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(p.initialCapacity)
                .maximumSize(p.maximumSize)
                //超时自动删除
                .expireAfterAccess(p.expireAfterAccessSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(p.expireAfterWriteSeconds, TimeUnit.SECONDS)
                .removalListener(new MyRemovalListener())
                .build(new DataLoader());
    }

    public final V get(K k) {
        try {
            return cache.get(k);
        } catch (ExecutionException e) {
            LoggerUtils.error("CacheContainer get error", e);
            throw new UncheckedExecutionException(e);
        }
    }

    public abstract V loadFromDb(K k) throws Exception;

    public final void put(K k, V v) {
        cache.put(k, v);
    }

    public final void remove(K k) {
        cache.invalidate(k);
    }

    public final ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    class DataLoader extends CacheLoader<K, V> {
        @Override
        public V load(K key) throws Exception {
            return loadFromDb(key);
        }
    }

    class MyRemovalListener implements RemovalListener<K, V> {
        @Override
        public void onRemoval(RemovalNotification<K, V> notification) {
        	//logger
        }
    }

}
