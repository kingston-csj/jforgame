package jforgame.commons.persist.container;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.HashShardingStrategy;
import jforgame.commons.persist.strategy.SavingStrategy;
import jforgame.commons.persist.strategy.ShardingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link PersistContainerGroup}
 */
public class PersistContainerGroupTest {

    private PersistContainerGroup group;
    private TestSavingStrategy savingStrategy;

    @Before
    public void setUp() {
        savingStrategy = new TestSavingStrategy();
        PersistContainer[] containers = new PersistContainer[2];
        containers[0] = new QueueContainer("worker-0", savingStrategy);
        containers[1] = new QueueContainer("worker-1", savingStrategy);
        group = new PersistContainerGroup("test-group", containers);
    }

    @After
    public void tearDown() {
        if (group != null) {
            group.shutdownGraceful();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullContainers() {
        new PersistContainerGroup("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyContainers() {
        new PersistContainerGroup("test", new PersistContainer[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullShardingStrategy() {
        PersistContainer[] containers = new PersistContainer[1];
        containers[0] = new QueueContainer("worker", new TestSavingStrategy());
        new PersistContainerGroup("test", containers, (ShardingStrategy) null);
    }

    @Test
    public void testReceiveEntity() {
        TestEntity entity = new TestEntity(1, "test");
        group.receive(entity);
        sleep(200);
    }

    @Test
    public void testSize() {
        assertEquals(0, group.size());
        TestEntity entity = new TestEntity(1, "test");
        group.receive(entity);
        sleep(50);
    }

    @Test
    public void testToString() {
        String str = group.toString();
        assertNotNull(str);
        assertTrue(str.contains("PersistContainerGroup"));
        assertTrue(str.contains("test-group"));
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
