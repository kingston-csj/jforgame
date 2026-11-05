package jforgame.commons.utils;

import jforgame.commons.ds.LruHashMap;
import org.junit.Assert;
import org.junit.Test;

public class TestLruHashMap {

	@Test
	public void testLru() {
		LruHashMap<Integer, Boolean> cache = new LruHashMap<>(8);
        for (int i = 1; i <= 8; i++) {
            cache.put(i,Boolean.TRUE);
        }
    	cache.get(1);
    	cache.put(9, Boolean.TRUE);
    	
    	Assert.assertTrue(cache.containsKey(1));
    	Assert.assertFalse(cache.containsKey(2));
	}
	
}
