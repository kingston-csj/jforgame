package jforgame.commons.eventbus;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link BaseEvent}
 */
public class BaseEventTest {

    @Test
    public void testGetOwnerDefault() {
        BaseEvent event = new TestEvent();
        assertEquals("system", event.getOwner());
    }

    @Test
    public void testCustomOwner() {
        BaseEvent event = new CustomOwnerEvent();
        assertEquals("custom-owner", event.getOwner());
    }

    // Test implementation classes
    private static class TestEvent implements BaseEvent {
    }

    private static class CustomOwnerEvent implements BaseEvent {
        @Override
        public Object getOwner() {
            return "custom-owner";
        }
    }
}
