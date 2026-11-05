package jforgame.commons.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link RandomWeightPool}
 */
public class RandomWeightPoolTest {

    private static class WeightedPool extends RandomWeightPool<String> {
        public WeightedPool(java.util.Collection<String> items) {
            super(items);
        }

        @Override
        public int getWeight(String element) {
            switch (element) {
                case "high": return 100;
                case "medium": return 50;
                case "low": return 10;
                default: return 1;
            }
        }
    }

    @Test
    public void testRandomOne() {
        WeightedPool pool = new WeightedPool(Arrays.asList("high", "medium", "low"));
        String result = pool.randomOne();
        assertNotNull(result);
        assertTrue(Arrays.asList("high", "medium", "low").contains(result));
    }

    @Test
    public void testRandomList() {
        WeightedPool pool = new WeightedPool(Arrays.asList("high", "medium", "low"));
        List<String> results = pool.randomList(2, false);
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void testRandomListWithRemove() {
        WeightedPool pool = new WeightedPool(Arrays.asList("high", "medium", "low"));
        List<String> results = pool.randomList(3, true);
        assertEquals(3, results.size());
        // After removing all, pool should have empty weights for removed items
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomListCountExceedsPool() {
        WeightedPool pool = new WeightedPool(Arrays.asList("a", "b"));
        pool.randomList(3, true);
    }

    @Test
    public void testZeroWeightItemsFiltered() {
        java.util.List<String> items = Arrays.asList("a", "b", "c");
        RandomWeightPool<String> pool = new RandomWeightPool<String>(items) {
            @Override
            public int getWeight(String element) {
                return 0; // All zero weight, should be filtered out
            }
        };
        // Should still be constructable, randomOne might throw or return exception
        // depending on implementation of randomIndex with empty weights
    }

    @Test
    public void testWeightedRandomness() {
        WeightedPool pool = new WeightedPool(Arrays.asList("high", "medium", "low"));
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;

        for (int i = 0; i < 1000; i++) {
            String result = pool.randomOne();
            switch (result) {
                case "high": highCount++; break;
                case "medium": mediumCount++; break;
                case "low": lowCount++; break;
            }
        }

        // "high" should be selected more often than "medium", which more than "low"
        assertTrue(highCount > lowCount);
    }
}
