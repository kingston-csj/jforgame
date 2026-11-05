package jforgame.commons.ds;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Pair}
 */
public class PairTest {

    @Test
    public void testConstructorAndGetters() {
        Pair<String, Integer> pair = new Pair<>("hello", 42);
        assertEquals("hello", pair.getFirst());
        assertEquals(Integer.valueOf(42), pair.getSecond());
    }

    @Test
    public void testWithNullValues() {
        Pair<String, String> pair = new Pair<>(null, null);
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void testMixedNullValues() {
        Pair<String, Integer> pair1 = new Pair<>(null, 42);
        assertNull(pair1.getFirst());
        assertEquals(Integer.valueOf(42), pair1.getSecond());

        Pair<String, Integer> pair2 = new Pair<>("hello", null);
        assertEquals("hello", pair2.getFirst());
        assertNull(pair2.getSecond());
    }

    @Test
    public void testToString() {
        Pair<String, Integer> pair = new Pair<>("hello", 42);
        String str = pair.toString();
        assertNotNull(str);
        assertTrue(str.contains("Pair"));
        assertTrue(str.contains("hello"));
        assertTrue(str.contains("42"));
    }

    @Test
    public void testGenericTypes() {
        Pair<Integer, Double> pair = new Pair<>(100, 3.14);
        assertEquals(Integer.valueOf(100), pair.getFirst());
        assertEquals(Double.valueOf(3.14), pair.getSecond());
    }
}
