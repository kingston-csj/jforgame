package jforgame.commons.ds;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Triple}
 */
public class TripleTest {

    @Test
    public void testConstructorAndGetters() {
        Triple<String, Integer, Boolean> triple = new Triple<>("hello", 42, true);
        assertEquals("hello", triple.getFirst());
        assertEquals(Integer.valueOf(42), triple.getSecond());
        assertEquals(Boolean.TRUE, triple.getThird());
    }

    @Test
    public void testWithNullValues() {
        Triple<String, String, String> triple = new Triple<>(null, null, null);
        assertNull(triple.getFirst());
        assertNull(triple.getSecond());
        assertNull(triple.getThird());
    }

    @Test
    public void testPartialNullValues() {
        Triple<String, Integer, Boolean> triple = new Triple<>("hello", null, false);
        assertEquals("hello", triple.getFirst());
        assertNull(triple.getSecond());
        assertEquals(Boolean.FALSE, triple.getThird());
    }

    @Test
    public void testToString() {
        Triple<String, Integer, Boolean> triple = new Triple<>("hello", 42, true);
        String str = triple.toString();
        assertNotNull(str);
        assertTrue(str.contains("Triple"));
        assertTrue(str.contains("hello"));
        assertTrue(str.contains("42"));
        assertTrue(str.contains("true"));
    }

    @Test
    public void testGenericTypes() {
        Triple<Integer, Double, Long> triple = new Triple<>(100, 3.14, 99999L);
        assertEquals(Integer.valueOf(100), triple.getFirst());
        assertEquals(Double.valueOf(3.14), triple.getSecond());
        assertEquals(Long.valueOf(99999L), triple.getThird());
    }
}
