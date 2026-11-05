package jforgame.commons.eventbus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link EventBus}
 * Note: EventBus.post() traverses class hierarchy via getSuperclass(),
 * which only follows class inheritance (extends), NOT interface implementation.
 * So parent events must use class hierarchy to be matched by child events.
 */
public class EventBusTest {

    private EventBus eventBus;

    @Before
    public void setUp() {
        eventBus = new EventBus();
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testRegisterAndPost() {
        TestSubscriber subscriber = new TestSubscriber();
        eventBus.register(subscriber);

        TestEvent event = new TestEvent("test message");
        eventBus.post(event);

        assertEquals(1, subscriber.receivedEvents.size());
        assertEquals("test message", subscriber.receivedEvents.get(0).message);
    }

    @Test
    public void testMultipleSubscribers() {
        TestSubscriber sub1 = new TestSubscriber();
        TestSubscriber sub2 = new TestSubscriber();
        eventBus.register(sub1);
        eventBus.register(sub2);

        TestEvent event = new TestEvent("broadcast");
        eventBus.post(event);

        assertEquals(1, sub1.receivedEvents.size());
        assertEquals(1, sub2.receivedEvents.size());
    }

    @Test
    public void testEventInheritance() {
        // ChildEvent extends ParentEvent extends Object (class hierarchy)
        // EventBus traverses superclass chain, so ParentEvent handlers
        // WILL receive ChildEvent
        ParentEventSubscriber parentSubscriber = new ParentEventSubscriber();
        ChildEventSubscriber childSubscriber = new ChildEventSubscriber();
        eventBus.register(parentSubscriber);
        eventBus.register(childSubscriber);

        ChildEvent childEvent = new ChildEvent();
        eventBus.post(childEvent);

        assertEquals("Parent subscriber should receive child event",
                1, parentSubscriber.receivedCount);
        assertEquals("Child subscriber should receive child event",
                1, childSubscriber.receivedCount);
    }

    @Test
    public void testAsyncPost() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AsyncTestSubscriber subscriber = new AsyncTestSubscriber(latch);
        eventBus.register(subscriber);

        TestEvent event = new TestEvent("async");
        eventBus.asyncPost(event);

        boolean received = latch.await(2, TimeUnit.SECONDS);
        assertTrue("Async event should be received within timeout", received);
        assertEquals(1, subscriber.receivedCount);
    }

    @Test
    public void testExceptionHandling() {
        ExceptionSubscriber subscriber = new ExceptionSubscriber();
        TestSubscriber normalSubscriber = new TestSubscriber();
        eventBus.register(subscriber);
        eventBus.register(normalSubscriber);

        TestEvent event = new TestEvent("exception test");
        eventBus.post(event);

        // Normal subscriber should still receive the event
        assertEquals(1, normalSubscriber.receivedEvents.size());
    }

    @Test
    public void testMultipleEvents() {
        TestSubscriber subscriber = new TestSubscriber();
        eventBus.register(subscriber);

        for (int i = 0; i < 5; i++) {
            eventBus.post(new TestEvent("msg-" + i));
        }

        assertEquals(5, subscriber.receivedEvents.size());
    }

    // --- Test event and subscriber classes ---

    private static class TestEvent implements BaseEvent {
        final String message;
        TestEvent(String message) {
            this.message = message;
        }
    }

    // Class hierarchy for testing inheritance (extends, not implements interface)
    private static class ParentEvent implements BaseEvent {
    }

    private static class ChildEvent extends ParentEvent {
    }

    private static class TestSubscriber {
        final List<TestEvent> receivedEvents = new ArrayList<>();

        @Subscribe
        public void onTestEvent(TestEvent event) {
            receivedEvents.add(event);
        }
    }

    private static class ParentEventSubscriber {
        int receivedCount = 0;

        @Subscribe
        public void onParentEvent(ParentEvent event) {
            receivedCount++;
        }
    }

    private static class ChildEventSubscriber {
        int receivedCount = 0;

        @Subscribe
        public void onChildEvent(ChildEvent event) {
            receivedCount++;
        }
    }

    private static class AsyncTestSubscriber {
        final CountDownLatch latch;
        int receivedCount = 0;

        AsyncTestSubscriber(CountDownLatch latch) {
            this.latch = latch;
        }

        @Subscribe
        public void onTestEvent(TestEvent event) {
            receivedCount++;
            latch.countDown();
        }
    }

    private static class ExceptionSubscriber {
        @Subscribe
        public void onTestEvent(TestEvent event) {
            throw new RuntimeException("Simulated exception in subscriber");
        }
    }
}
