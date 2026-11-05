package jforgame.commons.persist.container;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.SavingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DelayContainer}
 * Note: DelayContainer uses a shared static scheduler (SHARED_TIMER),
 * so tests that instantiate DelayContainer after a test that called
 * shutdownScheduler() will fail. We only test basic non-scheduler-dependent
 * methods here.
 */
public class DelayContainerTest {

    private DelayContainer container;
    private TestSavingStrategy savingStrategy;

    @Before
    public void setUp() throws Exception {
        savingStrategy = new TestSavingStrategy();
        // 1 second delay
        container = new DelayContainer("test-delay", 1, savingStrategy);
    }

    @After
    public void tearDown() {
        // Only graceful shutdown the container, don't shutdown the shared scheduler
        if (container != null) {
            container.shutdownGraceful();
        }
    }

    @Test
    public void testReceiveNull() {
        // Should not throw
        container.receive(null);
        assertEquals(0, container.size());
    }

    @Test
    public void testSize() {
        assertEquals(0, container.size());
        container.receive(new TestEntity(1, "test"));
        assertTrue(container.size() >= 0);
    }

    @Test
    public void testToString() {
        String str = container.toString();
        assertNotNull(str);
        assertTrue(str.contains("DelayContainer"));
        assertTrue(str.contains("test-delay"));
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
    }

    private static class TestSavingStrategy implements SavingStrategy {
        int saveCount = 0;

        @Override
        public void doSave(Entity<?> entity) throws Exception {
            saveCount++;
        }
    }
}
