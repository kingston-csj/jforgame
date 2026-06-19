package jforgame.commons.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-performance reflection invocation tool, using MethodHandle instead of traditional reflection
 *
 * @since 2.4.0
 */
public final class MethodHandleUtils {
    // Method caller cache (class -> method signature -> method caller)
    private static final Map<Class<?>, Map<String, MethodCaller>> METHOD_CALLER_CACHE = new ConcurrentHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private MethodHandleUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Invokes a method on an object
     *
     * @param target     the target object
     * @param methodName the method name
     * @param args       the argument list
     * @return the method return value
     * @throws ReflectiveOperationException reflection invocation exception
     */
    public static Object invoke(Object target, String methodName, Object... args) throws ReflectiveOperationException {
        if (target == null) {
            throw new NullPointerException("Target object is null");
        }
        return invokeInternal(target.getClass(), target, methodName, args);
    }

    /**
     * Invokes a static method
     *
     * @param clazz      the target class
     * @param methodName the method name
     * @param args       the argument list
     * @return the method return value
     * @throws ReflectiveOperationException reflection invocation exception
     */
    public static Object invokeStatic(Class<?> clazz, String methodName, Object... args) throws ReflectiveOperationException {
        return invokeInternal(clazz, null, methodName, args);
    }

    /**
     * Gets a method caller (for repeated invocation of the same method)
     *
     * @param clazz          the target class
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the method caller
     * @throws NoSuchMethodException method not found exception
     */
    public static MethodCaller getCaller(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        String methodSignature = generateMethodSignature(methodName, parameterTypes);
        return METHOD_CALLER_CACHE.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodSignature, k -> createMethodCaller(clazz, methodName, parameterTypes));
    }

    // Internal invocation implementation
    private static Object invokeInternal(Class<?> clazz, Object target, String methodName, Object[] args) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = getParameterTypes(args);
        MethodCaller caller = getCaller(clazz, methodName, parameterTypes);
        try {
            return caller.invoke(target, args);
        } catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
    }

    // Create method caller
    private static MethodCaller createMethodCaller(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        try {
            // Get the method
            Method method = findMethod(clazz, methodName, parameterTypes);
            method.setAccessible(true);

            // Create MethodHandle
            MethodHandle methodHandle = LOOKUP.unreflect(method);

            // Handle static methods
            if (Modifier.isStatic(method.getModifiers())) {
                return (target, args) -> {
                    try {
                        return methodHandle.invokeWithArguments(args);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };
            } else {
                return (target, args) -> {
                    try {
                        return methodHandle.bindTo(target).invokeWithArguments(args);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create method caller for " + clazz.getName() + "." + methodName, e);
        }
    }

    // Find method (supports auto boxing/unboxing and subclass matching)
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        // First try exact match
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // Then try fuzzy match (supports boxing/unboxing and subclass)
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && isMethodCompatible(method, parameterTypes)) {
                    return method;
                }
            }
            throw e;
        }
    }

    /**
     * Gets a method caller through a reflection Method object
     *
     * @param method the reflection Method object
     * @return the method caller
     */
    public static MethodCaller getCaller(Method method) {
        // Generate unique cache key (class name + method signature)
        String methodSignature = generateMethodSignature(method);
        return METHOD_CALLER_CACHE.computeIfAbsent(method.getDeclaringClass(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodSignature, k -> createMethodCaller(method));
    }

    // Generate method signature (for cache key)
    private static String generateMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(parameterTypes[i].getName());
        }
        return sb.append(")").toString();
    }

    // Create method caller based on Method object
    private static MethodCaller createMethodCaller(Method method) {
        try {
            method.setAccessible(true);
            MethodHandle methodHandle = LOOKUP.unreflect(method);

            // Handle static methods
            if (Modifier.isStatic(method.getModifiers())) {
                return (target, args) -> {
                    try {
                        return methodHandle.invokeWithArguments(args);
                    } catch (Throwable e) {
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new RuntimeException(e);
                    }
                };
            } else {
                return (target, args) -> {
                    try {
                        return methodHandle.bindTo(target).invokeWithArguments(args);
                    } catch (Throwable e) {
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new RuntimeException(e);
                    }
                };
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create method caller for " + method, e);
        }
    }

    // Check if method is compatible with parameter types
    private static boolean isMethodCompatible(Method method, Class<?>[] parameterTypes) {
        Class<?>[] methodParamTypes = method.getParameterTypes();
        if (methodParamTypes.length != parameterTypes.length) {
            return false;
        }

        for (int i = 0; i < methodParamTypes.length; i++) {
            if (!isAssignableFrom(methodParamTypes[i], parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    // Check if types are compatible (supports boxing/unboxing)
    private static boolean isAssignableFrom(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        // Handle primitive type and wrapper type relationship
        if (targetType.isPrimitive()) {
            return boxedType(targetType).isAssignableFrom(sourceType);
        } else if (sourceType.isPrimitive()) {
            return targetType.isAssignableFrom(boxedType(sourceType));
        }

        return false;
    }

    // Get the wrapper type of primitive type
    private static Class<?> boxedType(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == byte.class) return Byte.class;
        return primitiveType;
    }

    // Generate method signature
    private static String generateMethodSignature(String methodName, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName).append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(parameterTypes[i].getName());
        }
        return sb.append(")").toString();
    }

    // Get parameter type array
    private static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) {
            return new Class<?>[0];
        }
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i] == null ? Object.class : args[i].getClass();
        }
        return parameterTypes;
    }
}