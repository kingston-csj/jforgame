package jforgame.commons.util;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ClassScanner}
 */
public class ClassScannerTest {

    @Test
    public void testListClasses() {
        // Scan jforgame.commons.ds package
        Set<Class<?>> classes = ClassScanner.listClasses("jforgame.commons.ds");
        assertNotNull(classes);
        assertTrue(classes.size() > 0);
        // Should find our package classes
        assertTrue(classes.contains(jforgame.commons.ds.CircleQueue.class));
        assertTrue(classes.contains(jforgame.commons.ds.Pair.class));
        assertTrue(classes.contains(jforgame.commons.ds.Triple.class));
    }

    @Test
    public void testListClassesEmpty() {
        Set<Class<?>> classes = ClassScanner.listClasses("nonexistent.package");
        assertNotNull(classes);
        assertEquals(0, classes.size());
    }

    @Test
    public void testListAllSubclasses() {
        Set<Class<?>> classes = ClassScanner.listAllSubclasses("jforgame.commons.ds", Object.class);
        assertNotNull(classes);
        assertTrue(classes.size() > 0);
    }

    @Test
    public void testListClassesWithFilter() {
        Set<Class<?>> classes = ClassScanner.listClasses("jforgame.commons.ds",
                clazz -> clazz.getSimpleName().startsWith("C"));
        assertNotNull(classes);
        assertTrue(classes.contains(jforgame.commons.ds.CircleQueue.class));
        assertTrue(classes.contains(jforgame.commons.ds.ConcurrentHashSet.class));
    }

    @Test
    public void testListClassesWithFilterL() {
        Set<Class<?>> classes = ClassScanner.listClasses("jforgame.commons.ds",
                clazz -> clazz.getSimpleName().startsWith("L"));
        assertNotNull(classes);
        assertTrue(classes.contains(jforgame.commons.ds.LazyCacheMap.class));
        assertTrue(classes.contains(jforgame.commons.ds.LruHashMap.class));
    }
}
