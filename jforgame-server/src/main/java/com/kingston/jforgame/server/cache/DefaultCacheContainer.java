package com.kingston.jforgame.server.cache;

public class DefaultCacheContainer<K, V> extends AbstractCacheContainer<K, V> {

    private Persistable<K, V> persistable;

    public DefaultCacheContainer(Persistable<K, V> persistable, CacheOptions p) {
        super(p);
        this.persistable = persistable;
    }

    @Override
    public V loadOnce(K k) throws Exception {
        return persistable.load(k);
    }

}
