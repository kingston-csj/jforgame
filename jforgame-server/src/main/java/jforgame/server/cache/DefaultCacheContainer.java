package jforgame.server.cache;

import jforgame.server.db.BaseEntity;

public class DefaultCacheContainer<K, V extends BaseEntity> extends AbstractCacheContainer<K, V> {

	private Persistable<K, V> persistable;

	public DefaultCacheContainer(Persistable<K, V> persistable, CacheOptions p) {
		super(p);
		this.persistable = persistable;
	}

	@Override
	public V loadFromDb(K k) throws Exception {
		V entity = persistable.load(k);
		if (entity != null) {
			entity.markPersistent();
		}
		return entity;
	}

}
