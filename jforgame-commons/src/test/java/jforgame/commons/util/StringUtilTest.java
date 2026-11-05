package jforgame.commons.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link StringUtil}
 */
public class StringUtilTest {

    @Test
    public void testIsEmpty() {
        assertTrue(true);
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("test"));
        assertFalse(StringUtil.isEmpty(" "));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(false);
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty("test"));
        assertTrue(StringUtil.isNotEmpty(" "));
    }

    @Test
    public void testFirstLetterToUpperCase() {
        assertEquals("Hello", StringUtil.firstLetterToUpperCase("hello"));
        assertEquals("A", StringUtil.firstLetterToUpperCase("a"));
        assertEquals("Test", StringUtil.firstLetterToUpperCase("test"));
    }

    @Test
    public void testFirstLetterToUpperCaseAlreadyUpper() {
        assertEquals("Hello", StringUtil.firstLetterToUpperCase("Hello"));
    }

    @Test
    public void testFirstLetterToUpperCaseSingleChar() {
        assertEquals("X", StringUtil.firstLetterToUpperCase("x"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testFirstLetterToUpperCaseEmpty() {
        // Empty string will throw because sb.setCharAt(0, ...) on empty StringBuilder
        StringUtil.firstLetterToUpperCase("");
    }

    @Test
    public void testFirstLetterToLowerCase() {
        assertEquals("hello", StringUtil.firstLetterToLowerCase("Hello"));
        assertEquals("a", StringUtil.firstLetterToLowerCase("A"));
        assertEquals("test", StringUtil.firstLetterToLowerCase("Test"));
    }

    @Test
    public void testFirstLetterToLowerCaseAlreadyLower() {
        assertEquals("hello", StringUtil.firstLetterToLowerCase("hello"));
    }

    @Test
    public void testFirstLetterToLowerCaseSingleChar() {
        assertEquals("x", StringUtil.firstLetterToLowerCase("X"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testFirstLetterToLowerCaseEmpty() {
        // Empty string will throw because sb.setCharAt(0, ...) on empty StringBuilder
        StringUtil.firstLetterToLowerCase("");
    }
}
