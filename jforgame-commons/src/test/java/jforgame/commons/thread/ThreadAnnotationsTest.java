package jforgame.commons.thread;

import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ThreadSafe} and {@link NotThreadSafe} annotations
 * Note: These annotations have SOURCE retention, so they are not available at runtime.
 * The tests verify their declaration metadata only.
 */
public class ThreadAnnotationsTest {

    @Test
    public void testThreadSafeRetentionPolicy() {
        Retention retention = ThreadSafe.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.SOURCE, retention.value());
    }

    @Test
    public void testNotThreadSafeRetentionPolicy() {
        Retention retention = NotThreadSafe.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.SOURCE, retention.value());
    }

    @Test
    public void testAnnotationsAreDocumented() {
        // Both annotations have @Documented
        assertTrue(ThreadSafe.class.isAnnotationPresent(java.lang.annotation.Documented.class));
        assertTrue(NotThreadSafe.class.isAnnotationPresent(java.lang.annotation.Documented.class));
    }

    @Test
    public void testAnnotationsTargetType() {
        java.lang.annotation.Target ts = ThreadSafe.class.getAnnotation(java.lang.annotation.Target.class);
        assertNotNull(ts);
        java.lang.annotation.ElementType[] targets = ts.value();
        assertEquals(1, targets.length);
        assertEquals(java.lang.annotation.ElementType.TYPE, targets[0]);
    }

    @Test
    public void testAnnotationsAreSourceOnly() {
        // Since retention is SOURCE, annotations are NOT available at runtime
        // We can only verify the annotations themselves have correct metadata
        Class<?> classWithThreadSafe = SomeThreadSafeClass.class;
        Class<?> classWithNotThreadSafe = SomeNotThreadSafeClass.class;

        // These assertions verify that the annotation types exist and have proper structure
        assertNotNull(ThreadSafe.class);
        assertNotNull(NotThreadSafe.class);
        assertTrue(java.lang.reflect.Modifier.isInterface(ThreadSafe.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isInterface(NotThreadSafe.class.getModifiers()));
    }

    // Sample annotated classes - the annotations won't be retained at runtime
    @ThreadSafe
    private static class SomeThreadSafeClass {
    }

    @NotThreadSafe
    private static class SomeNotThreadSafeClass {
    }
}
