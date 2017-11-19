package com.kingston.jforgame.server.utils;

import org.junit.Assert;
import org.junit.Test;

import com.kingston.jforgame.common.utils.LruHashMap;


public class LruHashMapTest {
	
	@Test
	public void testLru() {
		LruHashMap<Integer, Integer> cache = new LruHashMap<>(5);
    	cache.put(1, 1);
    	cache.put(2, 2);
    	cache.put(3, 3);
    	cache.put(4, 4);
    	cache.put(5, 5);
    	cache.get(1);
    	cache.put(6, 6);
    	
    	Assert.assertTrue(cache.containsKey(1));
    	Assert.assertFalse(cache.containsKey(2));
	}

}
