package jforgame.commons.persist;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Entity} interface
 */
public class EntityTest {

    @Test
    public void testGetId() {
        TestEntity entity = new TestEntity(1001, "test");
        assertEquals(Integer.valueOf(1001), entity.getId());
    }

    @Test
    public void testGetKeyDefault() {
        TestEntity entity = new TestEntity(1001, "test");
        String key = entity.getKey();
        assertEquals("TestEntity@1001", key);
    }

    @Test
    public void testGetKeyWithStringId() {
        StringEntity entity = new StringEntity("abc123");
        String key = entity.getKey();
        assertEquals("StringEntity@abc123", key);
    }

    @Test
    public void testEntityWithComparable() {
        TestEntity e1 = new TestEntity(1001, "a");
        TestEntity e2 = new TestEntity(1002, "b");
        assertTrue(e1.getId().compareTo(e2.getId()) < 0);
    }

    // Test implementation classes
    private static class TestEntity implements Entity<Integer> {
        private final Integer id;
        private final String name;

        TestEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static class StringEntity implements Entity<String> {
        private final String id;

        StringEntity(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }
}
