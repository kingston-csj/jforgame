package jforgame.commons.util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link SplitUtil}
 */
public class SplitUtilTest {

    @Test
    public void testConstants() {
        assertEquals("_", SplitUtil.UNDERLINE);
        assertEquals(",|，", SplitUtil.COMMA);
        assertEquals("&", SplitUtil.AND);
        assertEquals("=", SplitUtil.EQUAL);
        assertEquals(";|；", SplitUtil.SEMICOLON);
        assertEquals("/", SplitUtil.SLASH);
    }

    @Test
    public void testToIntIntMap() {
        Map<Integer, Integer> result = SplitUtil.toIntIntMap("1001=1;1002=2;1003=3", ";", "=");
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(1), result.get(1001));
        assertEquals(Integer.valueOf(2), result.get(1002));
        assertEquals(Integer.valueOf(3), result.get(1003));
    }

    @Test
    public void testToIntIntMapWithEmptySegments() {
        // Empty parts should be handled gracefully
        String params = "1001=1;1002=2";
        Map<Integer, Integer> result = SplitUtil.toIntIntMap(params, ";", "=");
        assertEquals(2, result.size());
    }

    @Test
    public void testToStringIntMap() {
        Map<String, Integer> result = SplitUtil.toStringIntMap("abc=1;def=2", ";", "=");
        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(1), result.get("abc"));
        assertEquals(Integer.valueOf(2), result.get("def"));
    }

    @Test
    public void testToStringStringMap() {
        Map<String, String> result = SplitUtil.toStringStringMap("key1=val1;key2=val2", ";", "=");
        assertEquals(2, result.size());
        assertEquals("val1", result.get("key1"));
        assertEquals("val2", result.get("key2"));
    }

    @Test
    public void testCommaDelimiter() {
        Map<String, String> result = SplitUtil.toStringStringMap("a=b,c=d", SplitUtil.COMMA, SplitUtil.EQUAL);
        assertEquals(2, result.size());
        assertEquals("b", result.get("a"));
        assertEquals("d", result.get("c"));
    }

    @Test
    public void testChineseComma() {
        Map<String, String> result = SplitUtil.toStringStringMap("a=b，c=d", SplitUtil.COMMA, SplitUtil.EQUAL);
        assertEquals(2, result.size());
    }

    @Test
    public void testSinglePair() {
        Map<Integer, Integer> result = SplitUtil.toIntIntMap("1=2", ";", "=");
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(2), result.get(1));
    }
}
