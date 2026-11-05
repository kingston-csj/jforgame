package jforgame.commons.ds;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link LruHashMap}
 */
public class LruHashMapTest {

    private LruHashMap<String, String> lruMap;

    @Before
    public void setUp() {
        // MIN_MAX_CAPACITY is 8, so use 10 (above min) for default tests
        lruMap = new LruHashMap<>(10);
    }

    @Test
    public void testDefaultConstructor() {
        LruHashMap<String, String> defaultMap = new LruHashMap<>();
        assertEquals(1000, defaultMap.getMaxCapacity());
    }

    @Test
    public void testConstructorWithCapacity() {
        // Passing 10 should result in 10 (above MIN_MAX_CAPACITY of 8)
        assertEquals(10, lruMap.getMaxCapacity());
    }

    @Test
    public void testMinCapacityEnforcement() {
        // MIN_MAX_CAPACITY is 8
        LruHashMap<String, String> smallMap = new LruHashMap<>(3);
        assertEquals(8, smallMap.getMaxCapacity());
    }

    @Test
    public void testPutAndGet() {
        lruMap.put("key1", "value1");
        assertEquals("value1", lruMap.get("key1"));
    }

    @Test
    public void testPutReturnsOldValue() {
        lruMap.put("key1", "value1");
        assertEquals("value1", lruMap.put("key1", "value2"));
        assertEquals("value2", lruMap.get("key1"));
    }

    @Test
    public void testSize() {
        assertEquals(0, lruMap.size());
        lruMap.put("key1", "value1");
        assertEquals(1, lruMap.size());
    }

    @Test
    public void testContainsKey() {
        assertFalse(lruMap.containsKey("key1"));
        lruMap.put("key1", "value1");
        assertTrue(lruMap.containsKey("key1"));
    }

    @Test
    public void testRemove() {
        lruMap.put("key1", "value1");
        assertEquals("value1", lruMap.remove("key1"));
        assertFalse(lruMap.containsKey("key1"));
    }

    @Test
    public void testClear() {
        lruMap.put("key1", "value1");
        lruMap.put("key2", "value2");
        lruMap.clear();
        assertEquals(0, lruMap.size());
    }

    @Test
    public void testLruEviction() {
        // Use capacity=8 (MIN_MAX_CAPACITY), fill 9 entries to trigger eviction
        LruHashMap<String, String> smallMap = new LruHashMap<>(8);

        // Fill exactly to capacity (8)
        for (int i = 0; i < 8; i++) {
            smallMap.put("key" + i, "value" + i);
        }
        assertEquals(8, smallMap.size());

        // Add one more - should evict oldest
        smallMap.put("key8", "value8");

        // Size should still be 8 (oldest evicted)
        assertEquals(8, smallMap.size());
        // key8 should exist
        assertEquals("value8", smallMap.get("key8"));
        // key0 should have been evicted (least recently used)
        assertFalse(smallMap.containsKey("key0"));
    }

    @Test
    public void testLruAccessUpdatesOrder() {
        // Use capacity=8, fill entries, access one, then add new one
        LruHashMap<String, String> smallMap = new LruHashMap<>(8);
        for (int i = 0; i < 8; i++) {
            smallMap.put("key" + i, "value" + i);
        }
        // Access key0 to make it recently used
        smallMap.get("key0");
        // Add new entry - key1 should be evicted now (not key0)
        smallMap.put("key8", "value8");
        assertEquals(8, smallMap.size());
        // key0 should still exist because it was recently accessed
        assertEquals("value0", smallMap.get("key0"));
        // key1 should have been evicted
        assertFalse(smallMap.containsKey("key1"));
    }

    @Test
    public void testSetMaxCapacity() {
        LruHashMap<String, String> map = new LruHashMap<>(10);
        for (int i = 0; i < 10; i++) {
            map.put("key" + i, "value" + i);
        }
        assertEquals(10, map.size());

        // Set to smaller capacity (below MIN_MAX_CAPACITY gets enforced to 8)
        map.setMaxCapacity(5);
        assertEquals(8, map.getMaxCapacity());
        // Size should have been reduced
        assertTrue(map.size() <= 8);
    }

    @Test
    public void testSetMaxCapacityWithMinEnforcement() {
        lruMap.setMaxCapacity(3);
        // Should be enforced to MIN_MAX_CAPACITY = 8
        assertEquals(8, lruMap.getMaxCapacity());
    }

    @Test
    public void testGetMaxCapacity() {
        assertEquals(10, lruMap.getMaxCapacity());
    }
}
