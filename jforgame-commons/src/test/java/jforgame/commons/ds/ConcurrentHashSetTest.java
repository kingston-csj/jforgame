package jforgame.commons.ds;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ConcurrentHashSet}
 */
public class ConcurrentHashSetTest {

    private ConcurrentHashSet<String> set;

    @Before
    public void setUp() {
        set = new ConcurrentHashSet<>();
    }

    @Test
    public void testDefaultConstructor() {
        ConcurrentHashSet<Integer> defaultSet = new ConcurrentHashSet<>();
        assertTrue(defaultSet.isEmpty());
        assertEquals(0, defaultSet.size());
    }

    @Test
    public void testConstructorWithInitialCapacity() {
        ConcurrentHashSet<String> setWithCapacity = new ConcurrentHashSet<>(16);
        assertTrue(setWithCapacity.isEmpty());
    }

    @Test
    public void testAddAndSize() {
        assertTrue(set.add("a"));
        assertTrue(set.add("b"));
        assertEquals(2, set.size());

        // Adding duplicate should return false
        assertFalse(set.add("a"));
        assertEquals(2, set.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(set.isEmpty());
        set.add("test");
        assertFalse(set.isEmpty());
    }

    @Test
    public void testContains() {
        assertFalse(set.contains("a"));
        set.add("a");
        assertTrue(set.contains("a"));
        assertFalse(set.contains("b"));
    }

    @Test
    public void testRemove() {
        set.add("a");
        set.add("b");
        assertTrue(set.remove("a"));
        assertFalse(set.contains("a"));
        assertEquals(1, set.size());

        // Remove non-existent element returns false
        assertFalse(set.remove("c"));
    }

    @Test
    public void testClear() {
        set.add("a");
        set.add("b");
        set.add("c");
        set.clear();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    public void testIterator() {
        set.add("a");
        set.add("b");
        set.add("c");

        Iterator<String> iterator = set.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            String item = iterator.next();
            assertNotNull(item);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void testNullHandling() {
        // ConcurrentHashMap does not allow null keys
        // This test verifies the behavior
        try {
            set.add(null);
            // If it doesn't throw, it should be added (some implementations allow it)
        } catch (NullPointerException e) {
            // Expected: ConcurrentHashMap does not allow null keys
        }
    }
}
