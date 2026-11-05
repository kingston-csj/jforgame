package jforgame.commons.ds;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CircleQueue}
 */
public class CircleQueueTest {

    private CircleQueue<Integer> queue;

    @Before
    public void setUp() {
        queue = new CircleQueue<>(5);
    }

    @Test
    public void testDefaultConstructor() {
        CircleQueue<String> defaultQueue = new CircleQueue<>();
        assertEquals(100, defaultQueue.getCapacity());
        assertTrue(defaultQueue.isEmpty());
    }

    @Test
    public void testConstructorWithCapacity() {
        assertEquals(5, queue.getCapacity());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(queue.isEmpty());
        queue.add(1);
        assertFalse(queue.isEmpty());
    }

    @Test
    public void testAddAndSize() {
        assertEquals(0, queue.size());
        queue.add(1);
        assertEquals(1, queue.size());
        queue.add(2);
        assertEquals(2, queue.size());
    }

    @Test
    public void testAddBeyondCapacity() {
        // Add 5 elements to fill the queue
        for (int i = 0; i < 5; i++) {
            queue.add(i);
        }
        assertTrue(queue.isFull());
        assertEquals(5, queue.size());

        // Add one more element - should overwrite the oldest
        queue.add(5);
        assertEquals(5, queue.size());
        // The queue should now contain [1, 2, 3, 4, 5]
        Object[] elements = queue.getQueue();
        // getQueue returns capacity-length array; non-null entries are valid
        assertNotNull(elements);
        assertEquals(5, elements.length);
    }

    @Test
    public void testIsFull() {
        assertFalse(queue.isFull());
        for (int i = 0; i < 5; i++) {
            queue.add(i);
        }
        assertTrue(queue.isFull());
    }

    @Test
    public void testClear() {
        queue.add(1);
        queue.add(2);
        queue.add(3);
        assertFalse(queue.isEmpty());

        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    public void testGetQueue() {
        queue.add(10);
        queue.add(20);
        queue.add(30);
        Object[] elements = queue.getQueue();
        // Should return capacity-length array
        assertNotNull(elements);
        assertEquals(5, elements.length);
        assertTrue(elements[0] != null);
    }

    @Test
    public void testGetQueueWhenEmpty() {
        Object[] elements = queue.getQueue();
        assertNotNull(elements);
        assertEquals(5, elements.length);
    }

    @Test
    public void testAddAndRollOver() {
        // Fill the queue and then add more to trigger rollover
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        // Queue should keep latest 5 elements
        Object[] elements = queue.getQueue();
        assertNotNull(elements);
        assertEquals(5, elements.length);
        assertEquals(5, queue.size());
    }
}
