package jforgame.commons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link RandomUtil}
 */
public class RandomUtilTest {

    @Test
    public void testNextInt() {
        // Run multiple times to ensure no exceptions
        for (int i = 0; i < 100; i++) {
            int value = RandomUtil.nextInt();
            // Can't assert exact value, just check no exception
        }
    }

    @Test
    public void testNextIntWithBound() {
        for (int i = 0; i < 100; i++) {
            int value = RandomUtil.nextInt(10);
            assertTrue("Value should be in range [0, 10)", value >= 0 && value < 10);
        }
    }

    @Test
    public void testRandomValue() {
        for (int i = 0; i < 100; i++) {
            int value = RandomUtil.randomValue(5, 10);
            assertTrue("Value should be in range [5, 10]", value >= 5 && value <= 10);
        }
    }

    @Test
    public void testRandomValueSame() {
        assertEquals(5, RandomUtil.randomValue(5, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomValueMinGreaterThanMax() {
        RandomUtil.randomValue(10, 5);
    }

    @Test
    public void testRandomIndex() {
        List<Integer> probs = Arrays.asList(10, 20, 30);
        for (int i = 0; i < 100; i++) {
            int index = RandomUtil.randomIndex(probs);
            assertTrue("Index should be in range [0, 3)", index >= 0 && index < 3);
        }
    }

    @Test
    public void testRandomIndexEmpty() {
        assertEquals(-1, RandomUtil.randomIndex(new ArrayList<>()));
    }

    @Test
    public void testRandomIndexNull() {
        assertEquals(-1, RandomUtil.randomIndex(null));
    }

    @Test
    public void testRandomIndexList() {
        List<Integer> probs = Arrays.asList(10, 20, 30);
        List<Integer> indices = RandomUtil.randomIndexList(probs, 5, false);
        assertEquals(5, indices.size());
        for (int index : indices) {
            assertTrue(index >= 0 && index < 3);
        }
    }

    @Test
    public void testRandomIndexListWithRemove() {
        List<Integer> probs = new ArrayList<>(Arrays.asList(10, 20, 30));
        List<Integer> indices = RandomUtil.randomIndexList(probs, 3, true);
        assertEquals(3, indices.size());
        // After removing, all weights should be 0
        for (Integer p : probs) {
            assertEquals(Integer.valueOf(0), p);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomIndexListEmpty() {
        RandomUtil.randomIndexList(new ArrayList<>(), 5, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomIndexListNegative() {
        List<Integer> probs = Arrays.asList(10, 20, -5);
        RandomUtil.randomIndexList(probs, 5, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomIndexListCountTooLarge() {
        List<Integer> probs = Arrays.asList(10, 20, 30);
        RandomUtil.randomIndexList(probs, 10, true);
    }

    @Test
    public void testRandomOne() {
        List<String> items = Arrays.asList("a", "b", "c");
        String result = RandomUtil.randomOne(items, s -> 10); // All same weight
        assertNotNull(result);
        assertTrue(items.contains(result));
    }

    @Test
    public void testRandomOneZeroWeight() {
        List<String> items = Arrays.asList("a", "b", "c");
        String result = RandomUtil.randomOne(items, s -> 0);
        assertNull(result);
    }

    @Test
    public void testRandomOneWeighted() {
        List<Integer> items = Arrays.asList(1, 2, 3);
        // Item 2 has much higher weight
        Integer result = RandomUtil.randomOne(items, i -> i == 2 ? 1000 : 1);
        assertEquals(Integer.valueOf(2), result);
    }
}
