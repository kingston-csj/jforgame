package jforgame.commons.persist.dlq;

import jforgame.commons.persist.Entity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DeadLetter}
 */
public class DeadLetterTest {

    @Test
    public void testConstructorAndGetters() {
        TestEntity entity = new TestEntity(100, "test");
        DeadLetter dl = new DeadLetter("TestEntity@100", entity, 3, "database connection failed");

        assertEquals("TestEntity@100", dl.getKey());
        assertEquals(entity, dl.getEntity());
        assertEquals(3, dl.getRetryCount());
        assertEquals("database connection failed", dl.getLastErrorMessage());
        assertTrue(dl.getDeadAt() > 0);
        assertEquals(TestEntity.class.getName(), dl.getEntityClassName());
    }

    @Test
    public void testSetEntity() {
        TestEntity entity1 = new TestEntity(100, "v1");
        DeadLetter dl = new DeadLetter("key1", entity1, 1, "error1");
        assertEquals(entity1, dl.getEntity());

        TestEntity entity2 = new TestEntity(100, "v2");
        dl.setEntity(entity2);
        assertEquals(entity2, dl.getEntity());
    }

    @Test
    public void testNullEntity() {
        DeadLetter dl = new DeadLetter("key1", null, 1, "error");
        assertNull(dl.getEntity());
        assertEquals("unknown", dl.getEntityClassName());
    }

    @Test
    public void testNullErrorMessage() {
        TestEntity entity = new TestEntity(1, "test");
        DeadLetter dl = new DeadLetter("key1", entity, 1, null);
        assertNull(dl.getLastErrorMessage());
    }

    @Test
    public void testToString() {
        TestEntity entity = new TestEntity(100, "test");
        DeadLetter dl = new DeadLetter("TestEntity@100", entity, 3, "error");
        String str = dl.toString();
        assertNotNull(str);
        assertTrue(str.contains("DeadLetter"));
        assertTrue(str.contains("TestEntity@100"));
        assertTrue(str.contains("3"));
    }

    // Test entity class
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
}
