package jforgame.commons.persist.strategy;

import jforgame.commons.persist.Entity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link HashShardingStrategy}
 */
public class HashShardingStrategyTest {

    @Test
    public void testShardReturnsValidIndex() {
        HashShardingStrategy strategy = new HashShardingStrategy();
        TestEntity entity = new TestEntity(100);

        for (int workerCount = 1; workerCount <= 10; workerCount++) {
            int index = strategy.shard(entity, workerCount);
            assertTrue("Index should be in valid range", index >= 0 && index < workerCount);
        }
    }

    @Test
    public void testSameEntitySameIndex() {
        HashShardingStrategy strategy = new HashShardingStrategy();
        TestEntity entity = new TestEntity(100);

        int firstIndex = strategy.shard(entity, 4);
        for (int i = 0; i < 100; i++) {
            int index = strategy.shard(entity, 4);
            assertEquals("Same entity should always get same index", firstIndex, index);
        }
    }

    @Test
    public void testWorkerCountOne() {
        HashShardingStrategy strategy = new HashShardingStrategy();
        TestEntity entity = new TestEntity(100);

        int index = strategy.shard(entity, 1);
        assertEquals(0, index);
    }

    @Test
    public void testDifferentEntitiesDistributed() {
        HashShardingStrategy strategy = new HashShardingStrategy();
        int[] counts = new int[4];

        for (int i = 0; i < 1000; i++) {
            TestEntity entity = new TestEntity(i);
            int index = strategy.shard(entity, 4);
            counts[index]++;
        }

        // All indexes should have some distribution
        for (int count : counts) {
            assertTrue("Each worker should have some entities", count > 0);
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testWorkerCountZero() {
        HashShardingStrategy strategy = new HashShardingStrategy();
        TestEntity entity = new TestEntity(100);
        strategy.shard(entity, 0);
    }

    // Test entity class
    private static class TestEntity implements Entity<Integer> {
        private final Integer id;

        TestEntity(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }
}
