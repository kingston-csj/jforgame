package jforgame.commons.persist.container;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.SavingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CronContainer}
 */
public class CronContainerTest {

    private CronContainer container;
    private TestSavingStrategy savingStrategy;

    @Before
    public void setUp() {
        savingStrategy = new TestSavingStrategy();
        // Cron expression: run every second
        container = new CronContainer("test-cron", "0 * * * * ?", savingStrategy);
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
        assertEquals(1, container.size());
        // Give some time for next cron trigger
        sleep(65000);
    }

    @Test
    public void testSize() {
        assertEquals(0, container.size());
        container.receive(new TestEntity(1, "test"));
        assertEquals(1, container.size());
    }

    @Test
    public void testShutdown() {
        container.receive(new TestEntity(1, "test"));
        container.shutdownGraceful();
        // After shutdown, size may still be 0 or may have been flushed
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

        TestEntity(Integer id, String name) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    private static class TestSavingStrategy implements SavingStrategy {
        @Override
        public void doSave(Entity<?> entity) throws Exception {
        }
    }
}
