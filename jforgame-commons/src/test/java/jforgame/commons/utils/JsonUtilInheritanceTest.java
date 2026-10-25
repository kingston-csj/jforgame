package jforgame.commons.utils;

import jforgame.commons.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JsonUtilInheritanceTest {

    /** 父类：只有 private 字段，没有 getter/setter */
    static class Base {
        private long id;
        private String name;

        Base() {}

        Base(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /** 子类：扩展自己的字段 */
    static class Child extends Base {
        private int age;
        private String email;

        Child() {}

        Child(long id, String name, int age, String email) {
            super(id, name);
            this.age = age;
            this.email = email;
        }
    }

    /** 容器类：集合元素声明为父类类型，实际放入子类 */
    static class Container {
        private List<Base> items = new ArrayList<>();

        List<Base> getItems() { return items; }
        void addItem(Base item) { items.add(item); }
    }

    @Test
    public void testSerializeChildInCollection() {
        Container container = new Container();
        container.addItem(new Child(1, "Alice", 20, "alice@test.com"));
        container.addItem(new Child(2, "Bob", 25, "bob@test.com"));

        String json = JsonUtil.object2String(container);
        assertNotNull(json);
        System.out.println(json);

        // 父类字段 id、name 和子类字段 age、email 都应该出现在 JSON 中
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"age\":20"));
        assertTrue(json.contains("\"email\":\"alice@test.com\""));
    }

    @Test
    public void testDeserializeToConcreteType() {
        // 直接反序列化为 Child 类型，父类字段应能正确还原
        String json = "{\"id\":10,\"name\":\"Carol\",\"age\":30,\"email\":\"carol@test.com\"}";
        Child child = JsonUtil.string2Object(json, Child.class);

        assertNotNull(child);
        // 由于字段是 private 且没有 getter，这里用反射验证
        assertEquals(10L, reflectGet(child, "id"));
        assertEquals("Carol", reflectGet(child, "name"));
        assertEquals(30, reflectGet(child, "age"));
        assertEquals("carol@test.com", reflectGet(child, "email"));
    }

    @Test
    public void testDeserializeCollectionWithInheritance() {
        String json = "[{\"id\":1,\"name\":\"A\",\"age\":10,\"email\":\"a@t.com\"},"
                + "{\"id\":2,\"name\":\"B\",\"age\":20,\"email\":\"b@t.com\"}]";

        List<Child> list = JsonUtil.string2Collection(json, ArrayList.class, Child.class);
        assertNotNull(list);
        assertEquals(2, list.size());

        assertEquals(1L, reflectGet(list.get(0), "id"));
        assertEquals("A", reflectGet(list.get(0), "name"));
        assertEquals(10, reflectGet(list.get(0), "age"));
    }

    /** 用反射读取 private 字段的值，避免给测试类加 getter */
    private Object reflectGet(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new AssertionError("Failed to read field: " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(name)) return f;
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalArgumentException("Field not found: " + name);
    }
}