package jforgame.commons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link JsonUtil}
 */
public class JsonUtilTest {

    @Test
    public void testObject2String() {
        TestObject obj = new TestObject("hello", 42);
        String json = JsonUtil.object2String(obj);
        assertNotNull(json);
        assertTrue(json.contains("hello"));
        assertTrue(json.contains("42"));
    }

    @Test
    public void testObject2StringNull() {
        assertNull(JsonUtil.object2String(null));
    }

    @Test
    public void testString2Object() {
        String json = "{\"name\":\"test\",\"value\":100}";
        TestObject obj = JsonUtil.string2Object(json, TestObject.class);
        assertNotNull(obj);
        assertEquals("test", obj.name);
        assertEquals(Integer.valueOf(100), obj.value);
    }

    @Test
    public void testString2ObjectNull() {
        assertNull(JsonUtil.string2Object(null, TestObject.class));
    }

    @Test
    public void testString2ObjectEmpty() {
        assertNull(JsonUtil.string2Object("", TestObject.class));
    }

    @Test
    public void testString2ObjectInvalid() {
        try {
            JsonUtil.string2Object("invalid json", TestObject.class);
            fail("Should have thrown JsonParseException");
        } catch (JsonUtil.JsonParseException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMap2String() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        String json = JsonUtil.map2String(map);
        assertNotNull(json);
        assertTrue(json.contains("\"a\""));
        assertTrue(json.contains("1"));
    }

    @Test
    public void testString2Map() {
        String json = "{\"key1\":\"value1\",\"key2\":123}";
        Map<String, Object> map = JsonUtil.string2Map(json);
        assertNotNull(map);
        assertEquals("value1", map.get("key1"));
    }

    @Test
    public void testString2MapWithTypes() {
        String json = "{\"1\":\"one\",\"2\":\"two\"}";
        Map<String, String> map = JsonUtil.string2Map(json, String.class, String.class);
        assertNotNull(map);
        assertEquals("one", map.get("1"));
        assertEquals("two", map.get("2"));
    }

    @Test
    public void testString2Array() {
        String json = "[\"a\",\"b\",\"c\"]";
        String[] arr = JsonUtil.string2Array(json, String.class);
        assertNotNull(arr);
        assertEquals(3, arr.length);
        assertEquals("a", arr[0]);
        assertEquals("b", arr[1]);
        assertEquals("c", arr[2]);
    }

    @Test
    public void testString2Collection() {
        String json = "[1,2,3]";
        List<Integer> list = JsonUtil.string2Collection(json, ArrayList.class, Integer.class);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
    }

    @Test
    public void testNullFieldsIgnored() {
        TestObject obj = new TestObject("test", null);
        String json = JsonUtil.object2String(obj);
        assertNotNull(json);
        assertTrue(json.contains("test"));
        // Null fields should not appear
        assertFalse(json.contains("value"));
    }

    @Test
    public void testRoundTrip() {
        TestObject obj = new TestObject("roundtrip", 999);
        String json = JsonUtil.object2String(obj);
        TestObject parsed = JsonUtil.string2Object(json, TestObject.class);
        assertEquals(obj.name, parsed.name);
        assertEquals(obj.value, parsed.value);
    }

    // Test POJO class
    public static class TestObject {
        public String name;
        public Integer value;

        public TestObject() {
        }

        public TestObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
}
