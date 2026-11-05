package jforgame.commons.ds;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link LazyCacheMap}
 */
public class LazyCacheMapTest {

    private LazyCacheMap<String, String> cache;

    @Before
    public void setUp() {
        // capacity=3, aliveTime=60000ms
        cache = new LazyCacheMap<>(3, 60000L);
    }

    @Test
    public void testPutAndGet() {
        assertNull(cache.put("key1", "value1"));
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    public void testPutReturnsOldValue() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.put("key1", "value2"));
        assertEquals("value2", cache.get("key1"));
    }

    @Test
    public void testSize() {
        assertEquals(0, cache.size());
        cache.put("key1", "value1");
        assertEquals(1, cache.size());
        cache.put("key2", "value2");
        assertEquals(2, cache.size());
    }

    @Test
    public void testGetNonExistentKey() {
        assertNull(cache.get("nonexistent"));
    }

    @Test
    public void testRemove() {
        cache.put("key1", "value1");
        cache.remove("key1");
        assertNull(cache.get("key1"));
        assertEquals(0, cache.size());
    }

    @Test
    public void testClear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    public void testGetAllRecords() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        List<String> records = cache.getAllRecords();
        assertEquals(2, records.size());
        assertTrue(records.contains("value1"));
        assertTrue(records.contains("value2"));
    }

    @Test
    public void testCapacityOverflow() {
        // Fill beyond capacity (3)
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4"); // Should evict eldest
        assertTrue(cache.size() <= 3);
    }

    @Test
    public void testExpiredEntry() throws InterruptedException {
        // Create a cache with very short alive time
        LazyCacheMap<String, String> shortCache = new LazyCacheMap<>(10, 50L);
        shortCache.put("key1", "value1");

        // Wait for entry to expire
        Thread.sleep(100L);

        // Should return null for expired entry
        assertNull(shortCache.get("key1"));
    }

    @Test
    public void testLruMode() {
        // Create cache with LRU enabled
        LazyCacheMap<String, String> lruCache = new LazyCacheMap<>(2, 60000L, true);
        lruCache.put("key1", "value1");
        lruCache.put("key2", "value2");

        // Access key1 to make it recently used
        lruCache.get("key1");

        // Add new entry - should evict key2 (least recently used)
        lruCache.put("key3", "value3");

        // key1 should still exist because it was recently accessed
        assertEquals("value1", lruCache.get("key1"));
    }

    @Test
    public void testToString() {
        String str = cache.toString();
        assertNotNull(str);
        assertTrue(str.contains("LinkedCacheMap"));
    }
}
