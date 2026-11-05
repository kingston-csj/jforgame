package jforgame.commons.thread;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link NamedThreadFactory}
 */
public class NamedThreadFactoryTest {

    @Test
    public void testConstructor() {
        NamedThreadFactory factory = new NamedThreadFactory("test-group");
        Thread thread = factory.newThread(() -> {});
        assertNotNull(thread);
        assertEquals("test-group-thread-1", thread.getName());
        assertFalse(thread.isDaemon());
    }

    @Test
    public void testDaemonThread() {
        NamedThreadFactory factory = new NamedThreadFactory("daemon-group", true);
        Thread thread = factory.newThread(() -> {});
        assertTrue(thread.isDaemon());
    }

    @Test
    public void testNonDaemonThread() {
        NamedThreadFactory factory = new NamedThreadFactory("non-daemon", false);
        Thread thread = factory.newThread(() -> {});
        assertFalse(thread.isDaemon());
    }

    @Test
    public void testIncrementalThreadNames() {
        NamedThreadFactory factory = new NamedThreadFactory("counter");
        Thread t1 = factory.newThread(() -> {});
        Thread t2 = factory.newThread(() -> {});
        Thread t3 = factory.newThread(() -> {});

        assertEquals("counter-thread-1", t1.getName());
        assertEquals("counter-thread-2", t2.getName());
        assertEquals("counter-thread-3", t3.getName());
    }

    @Test
    public void testDefaultPriority() {
        NamedThreadFactory factory = new NamedThreadFactory("priority-test");
        Thread thread = factory.newThread(() -> {});
        assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
    }

    @Test
    public void testRunnableExecution() throws InterruptedException {
        NamedThreadFactory factory = new NamedThreadFactory("exec-test");
        final boolean[] executed = {false};
        Thread thread = factory.newThread(() -> {
            executed[0] = true;
        });
        thread.start();
        thread.join(2000);
        assertTrue("Runnable should have been executed", executed[0]);
    }
}
