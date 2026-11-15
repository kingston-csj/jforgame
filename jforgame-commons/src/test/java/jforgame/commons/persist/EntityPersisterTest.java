package jforgame.commons.persist;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link EntityPersister} interface
 */
public class EntityPersisterTest {

    @Test
    public void testInterfaceMethods() {
        // Verify that interface defines expected methods
        assertEquals(3, EntityPersister.class.getDeclaredMethods().length);
    }

    @Test
    public void testSaveToDb() {
        TestEntity entity = new TestEntity(1, "test");
        TestEntityPersister service = new TestEntityPersister();
        assertFalse(service.saveCalled);

        service.saveToDb(entity);
        assertTrue(service.saveCalled);
        assertEquals(entity, service.lastSaved);
    }

    @Test
    public void testDeleteFromDb() {
        TestEntity entity = new TestEntity(1, "test");
        TestEntityPersister service = new TestEntityPersister();
        assertFalse(service.deleteCalled);

        service.deleteFromDb(entity);
        assertTrue(service.deleteCalled);
        assertEquals(entity, service.lastDeleted);
    }

    @Test
    public void testShutDown() {
        TestEntityPersister service = new TestEntityPersister();
        assertFalse(service.shutdownCalled);

        service.shutDown();
        assertTrue(service.shutdownCalled);
    }

    // Test implementation classes
    private static class TestEntity implements Entity<Integer> {
        private final Integer id;

        TestEntity(Integer id, String name) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    private static class TestEntityPersister implements EntityPersister {
        boolean saveCalled = false;
        boolean deleteCalled = false;
        boolean shutdownCalled = false;
        Entity<?> lastSaved = null;
        Entity<?> lastDeleted = null;

        @Override
        public void saveToDb(Entity<?> entity) {
            saveCalled = true;
            lastSaved = entity;
        }

        @Override
        public void deleteFromDb(Entity<?> entity) {
            deleteCalled = true;
            lastDeleted = entity;
        }

        @Override
        public void shutDown() {
            shutdownCalled = true;
        }
    }
}
