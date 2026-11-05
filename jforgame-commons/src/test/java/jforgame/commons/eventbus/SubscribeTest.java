package jforgame.commons.eventbus;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Subscribe} annotation
 */
public class SubscribeTest {

    @Test
    public void testRetentionPolicy() {
        Retention retention = Subscribe.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    public void testTarget() {
        Target target = Subscribe.class.getAnnotation(Target.class);
        assertNotNull(target);
    }

    @Test
    public void testMethodAnnotation() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("onEvent", BaseEvent.class);
        assertTrue(method.isAnnotationPresent(Subscribe.class));
    }

    @Test
    public void testNonAnnotatedMethod() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("normalMethod");
        assertFalse(method.isAnnotationPresent(Subscribe.class));
    }

    // Sample class for testing
    private static class SampleClass {
        @Subscribe
        public void onEvent(BaseEvent event) {
        }

        public void normalMethod() {
        }
    }
}
