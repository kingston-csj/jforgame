package jforgame.commons.reflection;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MethodCaller}
 */
public class MethodCallerTest {

    @Test
    public void testFunctionalInterface() throws Exception {
        MethodCaller caller = (target, params) -> {
            return target.toString();
        };

        Object result = caller.invoke(new Object(), new Object[0]);
        assertNotNull(result);
    }

    @Test
    public void testWithParams() throws Exception {
        MethodCaller caller = (target, params) -> {
            int sum = 0;
            for (Object p : params) {
                sum += (Integer) p;
            }
            return sum;
        };

        Object result = caller.invoke(null, new Object[]{1, 2, 3});
        assertEquals(6, result);
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionPropagation() throws Exception {
        MethodCaller caller = (target, params) -> {
            throw new RuntimeException("test error");
        };
        caller.invoke(null, new Object[0]);
    }
}
