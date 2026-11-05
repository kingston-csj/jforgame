package jforgame.commons.persist.container;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.SavingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link QueueContainer}
 */
public class QueueContainerTest {

    private QueueContainer container;
    private TestSavingStrategy savingStrategy;

    @Before
    public void setUp() throws Exception {
        savingStrategy = new TestSavingStrategy();
        container = new QueueContainer("test-queue", savingStrategy);
    }

    @After
    public void tearDown() {
        if (container != null) {
            container.shutdownGraceful();
        }
    }

    @Test
    public void testReceiveEntity() {
        TestEntity entity = new TestEntity(1, "test");
        container.receive(entity);
        // Give the worker thread some time
        sleep(100);
    }

    @Test
    public void testMultipleReceive() {
        for (int i = 0; i < 5; i++) {
            TestEntity entity = new TestEntity(i, "test-" + i);
            container.receive(entity);
        }
        sleep(200);
    }

    @Test
    public void testSize() {
        assertEquals(0, container.size());
        container.receive(new TestEntity(1, "test"));
        sleep(50);
    }

    @Test
    public void testToString() {
        String str = container.toString();
        assertNotNull(str);
        assertTrue(str.contains("QueueContainer"));
        assertTrue(str.contains("test-queue"));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    private static class TestSavingStrategy implements SavingStrategy {
        int saveCount = 0;

        @Override
        public void doSave(Entity<?> entity) throws Exception {
            saveCount++;
        }
    }
}
