package jforgame.commons.utils;

import jforgame.commons.ds.LazyCacheMap;
import org.junit.Assert;
import org.junit.Test;

public class TestLazyCacheMap {
	
	@Test
	public void testCapacity() {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(3, 100, true);
		for (int i = 0; i < 100; i++) {
			cache.put(String.valueOf(i), String.valueOf(i));
		}

        Assert.assertEquals(3, cache.size());
	}

	@Test
	public void testLru() {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(3, 100, true);

		cache.put("a", "a");
		cache.put("b", "b");
		cache.put("c", "c");

		cache.get("a");
		cache.put("d", "d");

        Assert.assertNotNull(cache.get("c"));
        Assert.assertNotNull(cache.get("a"));
        Assert.assertNotNull(cache.get("d"));
        Assert.assertNull(cache.get("b"));
	}
	
	@Test
	public void testTimeout() throws Exception {
		LazyCacheMap<String, String> cache = new LazyCacheMap<>(2, 300, true);

		cache.put("a", "a");
		cache.put("b", "b");
		
		
		Thread.sleep(200);
		Assert.assertNotNull(cache.get("a"));
		cache.put("c", "c");

		Thread.sleep(100);
        Assert.assertNotNull(cache.get("c"));
        Assert.assertNull(cache.get("b"));
	}

}
