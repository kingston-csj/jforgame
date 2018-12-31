package com.kingston.jforgame.server.cache;

import com.kingston.jforgame.server.db.BaseEntity;

public class DefaultCacheContainer<K, V extends BaseEntity> extends AbstractCacheContainer<K, V> {

    private Persistable<K, V> persistable;

    public DefaultCacheContainer(Persistable<K, V> persistable, CacheOptions p) {
        super(p);
        this.persistable = persistable;
    }

    @Override
    public V loadOnce(K k) throws Exception {
    	V entity = persistable.load(k);
    	entity.markPersistent();
        return entity;
    }

}
