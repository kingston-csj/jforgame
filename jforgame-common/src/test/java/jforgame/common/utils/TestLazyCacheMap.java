package jforgame.common.utils;

import jforgame.common.ds.LazyCacheMap;
import org.junit.Assert;
import org.junit.Test;

public class TestLazyCacheMap {
	
	@Test
	public void testCapacity() {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(3, 100, true);
		for (int i = 0; i < 100; i++) {
			cache.put(String.valueOf(i), String.valueOf(i));
		}
		
		Assert.assertTrue(cache.size() == 3);
	}

	@Test
	public void testLru() {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(3, 100, true);

		cache.put("a", "a");
		cache.put("b", "b");
		cache.put("c", "c");

		cache.get("a");
		cache.put("d", "d");

		Assert.assertTrue(cache.get("c") != null);
		Assert.assertTrue(cache.get("a") != null);
		Assert.assertTrue(cache.get("d") != null);
		Assert.assertTrue(cache.get("b") == null);
	}
	
	@Test
	public void testTimeout() throws Exception {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(3, 100, true);

		cache.put("a", "a");
		cache.put("b", "b");
		
		
		Thread.sleep(150);
		cache.put("c", "c");
		
		Assert.assertTrue(cache.get("b") != null);
		Assert.assertTrue(cache.get("c") != null);
		Assert.assertTrue(cache.get("a") == null);
	}

}
