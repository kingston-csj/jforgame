package jforgame.commons.reflection;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MethodHandleUtils}
 */
public class MethodHandleUtilsTest {

    // Test target class
    private static class TestTarget {
        public String greet(String name) {
            return "Hello, " + name + "!";
        }

        public static int add(int a, int b) {
            return a + b;
        }

        public String noParams() {
            return "no params result";
        }
    }

    @Test
    public void testInvokeInstanceMethod() throws Exception {
        TestTarget target = new TestTarget();
        Object result = MethodHandleUtils.invoke(target, "greet", "World");
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testInvokeStaticMethod() throws Exception {
        Object result = MethodHandleUtils.invokeStatic(TestTarget.class, "add", 3, 5);
        assertEquals(8, result);
    }

    @Test
    public void testInvokeNoParams() throws Exception {
        TestTarget target = new TestTarget();
        Object result = MethodHandleUtils.invoke(target, "noParams");
        assertEquals("no params result", result);
    }

    @Test(expected = NullPointerException.class)
    public void testInvokeNullTarget() throws Exception {
        MethodHandleUtils.invoke(null, "greet", "World");
    }

    @Test
    public void testInvokeNonExistentMethod() {
        try {
            TestTarget target = new TestTarget();
            MethodHandleUtils.invoke(target, "nonExistent");
            fail("Should have thrown an exception");
        } catch (ReflectiveOperationException e) {
            // Expected - the wrapper exception type may vary depending on the path
            assertTrue(true);
        } catch (RuntimeException e) {
            // Also acceptable - some code paths wrap in RuntimeException
            assertTrue(true);
        }
    }

    @Test
    public void testGetCallerByMethodName() throws Exception {
        MethodCaller caller = MethodHandleUtils.getCaller(TestTarget.class, "greet", String.class);
        TestTarget target = new TestTarget();
        Object result = caller.invoke(target, new Object[]{"Test"});
        assertEquals("Hello, Test!", result);
    }

    @Test
    public void testGetCallerByReflectionMethod() throws Exception {
        java.lang.reflect.Method method = TestTarget.class.getMethod("greet", String.class);
        MethodCaller caller = MethodHandleUtils.getCaller(method);
        TestTarget target = new TestTarget();
        Object result = caller.invoke(target, new Object[]{"Reflection"});
        assertEquals("Hello, Reflection!", result);
    }

    @Test
    public void testInvokePrimitiveType() throws Exception {
        // Test with int and Integer (auto-boxing)
        Object result = MethodHandleUtils.invokeStatic(TestTarget.class, "add", 3, 7);
        assertEquals(10, result);
    }

    @Test
    public void testCallerCacheReuse() throws Exception {
        MethodCaller caller1 = MethodHandleUtils.getCaller(TestTarget.class, "greet", String.class);
        MethodCaller caller2 = MethodHandleUtils.getCaller(TestTarget.class, "greet", String.class);
        // Should be same instance from cache
        assertSame(caller1, caller2);
    }
}
