package jforgame.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link NumberUtil}
 */
public class NumberUtilTest {

    // --- booleanValue tests ---

    @Test
    public void testBooleanValueTrue() {
        assertTrue(NumberUtil.booleanValue("true"));
        assertTrue(NumberUtil.booleanValue("TRUE"));
        assertTrue(NumberUtil.booleanValue("True"));
        assertTrue(NumberUtil.booleanValue("1"));
    }

    @Test
    public void testBooleanValueFalse() {
        assertFalse(NumberUtil.booleanValue("false"));
        assertFalse(NumberUtil.booleanValue("FALSE"));
        assertFalse(NumberUtil.booleanValue("0"));
    }

    @Test
    public void testBooleanValueFromBoolean() {
        assertTrue(NumberUtil.booleanValue(Boolean.TRUE));
        assertFalse(NumberUtil.booleanValue(Boolean.FALSE));
    }

    @Test
    public void testBooleanValueDefault() {
        assertEquals(false, NumberUtil.booleanValue(null));
        assertEquals(true, NumberUtil.booleanValue(null, true));
        assertEquals(false, NumberUtil.booleanValue(null, false));
    }

    @Test
    public void testBooleanValueWithWhitespace() {
        assertTrue(NumberUtil.booleanValue("  true  "));
        assertFalse(NumberUtil.booleanValue("  0  "));
    }

    // --- intValue tests ---

    @Test
    public void testIntValue() {
        assertEquals(42, NumberUtil.intValue("42"));
        assertEquals(0, NumberUtil.intValue(null));
        assertEquals(10, NumberUtil.intValue(null, 10));
    }

    @Test
    public void testIntValueInvalid() {
        assertEquals(0, NumberUtil.intValue("invalid"));
        assertEquals(5, NumberUtil.intValue("invalid", 5));
    }

    @Test
    public void testIntValueWithWhitespace() {
        assertEquals(100, NumberUtil.intValue("  100  "));
    }

    // --- longValue tests ---

    @Test
    public void testLongValue() {
        assertEquals(9999999999L, NumberUtil.longValue("9999999999"));
        assertEquals(0L, NumberUtil.longValue(null));
        assertEquals(100L, NumberUtil.longValue(null, 100L));
    }

    @Test
    public void testLongValueInvalid() {
        assertEquals(0L, NumberUtil.longValue("invalid"));
        assertEquals(50L, NumberUtil.longValue("invalid", 50L));
    }

    // --- doubleValue tests ---

    @Test
    public void testDoubleValue() {
        assertEquals(3.14, NumberUtil.doubleValue("3.14"), 0.001);
        assertEquals(0.0, NumberUtil.doubleValue(null), 0.001);
        assertEquals(2.71, NumberUtil.doubleValue(null, 2.71), 0.001);
    }

    @Test
    public void testDoubleValueInvalid() {
        assertEquals(0.0, NumberUtil.doubleValue("invalid"), 0.001);
        assertEquals(1.5, NumberUtil.doubleValue("invalid", 1.5), 0.001);
    }

    // --- byteValue tests ---

    @Test
    public void testByteValue() {
        assertEquals((byte) 127, NumberUtil.byteValue("127"));
        assertEquals((byte) 0, NumberUtil.byteValue(null));
        assertEquals((byte) 10, NumberUtil.byteValue(null, (byte) 10));
    }

    @Test
    public void testByteValueInvalid() {
        assertEquals((byte) 0, NumberUtil.byteValue("invalid"));
        assertEquals((byte) 5, NumberUtil.byteValue("invalid", (byte) 5));
    }

    // --- shortValue tests ---

    @Test
    public void testShortValue() {
        assertEquals((short) 32767, NumberUtil.shortValue("32767"));
        assertEquals((short) 0, NumberUtil.shortValue(null));
        assertEquals((short) 100, NumberUtil.shortValue(null, (short) 100));
    }

    @Test
    public void testShortValueInvalid() {
        assertEquals((short) 0, NumberUtil.shortValue("invalid"));
        assertEquals((short) 50, NumberUtil.shortValue("invalid", (short) 50));
    }

    // --- Additional edge cases ---

    @Test
    public void testNumericString() {
        assertEquals(100, NumberUtil.intValue(100));
        assertEquals(200L, NumberUtil.longValue(200L));
        assertEquals(1.5, NumberUtil.doubleValue(1.5), 0.001);
    }

    @Test
    public void testNegativeValues() {
        assertEquals(-10, NumberUtil.intValue("-10"));
        assertEquals(-100L, NumberUtil.longValue("-100"));
        assertEquals(-3.14, NumberUtil.doubleValue("-3.14"), 0.001);
    }
}
