package com.kingston.jforgame.server.cache;

public interface EntityBuilder<K, V> {
	
	V newEntity(K key);

}
