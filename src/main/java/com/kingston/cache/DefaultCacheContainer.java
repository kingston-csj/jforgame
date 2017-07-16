package com.kingston.cache;

public class DefaultCacheContainer<K, V> extends CacheContainer<K, V> {

    private Persistable<K, V> persistable;

    public DefaultCacheContainer(Persistable<K, V> persistable, CacheOptions p) {
        super(p);
        this.persistable = persistable;
    }

    @Override
    public V loadOnce(K k) throws Exception {
        return persistable.load(k);
    }

//    @Override
//    public void save(K k, V v) throws PersistenceException {
//        persistable.save(k, v);
//    }

}
