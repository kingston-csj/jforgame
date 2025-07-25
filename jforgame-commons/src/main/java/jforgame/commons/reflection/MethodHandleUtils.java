package jforgame.commons.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能反射调用工具，使用 MethodHandle 替代传统反射
 * @since 2.4.0
 */
public final class MethodHandleUtils {
    // 方法调用器缓存（类 -> 方法签名 -> 方法调用器）
    private static final Map<Class<?>, Map<String, MethodCaller>> METHOD_CALLER_CACHE = new ConcurrentHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private MethodHandleUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 调用对象的方法
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 方法返回值
     * @throws ReflectiveOperationException 反射调用异常
     */
    public static Object invoke(Object target, String methodName, Object... args) throws ReflectiveOperationException {
        if (target == null) {
            throw new NullPointerException("Target object is null");
        }
        return invokeInternal(target.getClass(), target, methodName, args);
    }

    /**
     * 调用静态方法
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @param args       参数列表
     * @return 方法返回值
     * @throws ReflectiveOperationException 反射调用异常
     */
    public static Object invokeStatic(Class<?> clazz, String methodName, Object... args) throws ReflectiveOperationException {
        return invokeInternal(clazz, null, methodName, args);
    }

    /**
     * 获取方法调用器（用于重复调用同一方法）
     *
     * @param clazz          目标类
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法调用器
     * @throws NoSuchMethodException 方法不存在异常
     */
    public static MethodCaller getCaller(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        String methodSignature = generateMethodSignature(methodName, parameterTypes);
        return METHOD_CALLER_CACHE.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodSignature, k -> createMethodCaller(clazz, methodName, parameterTypes));
    }

    // 内部调用实现
    private static Object invokeInternal(Class<?> clazz, Object target, String methodName, Object[] args) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = getParameterTypes(args);
        MethodCaller caller = getCaller(clazz, methodName, parameterTypes);
        try {
            return caller.invoke(target, args);
        } catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
    }

    // 创建方法调用器
    private static MethodCaller createMethodCaller(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        try {
            // 获取方法
            Method method = findMethod(clazz, methodName, parameterTypes);
            method.setAccessible(true);

            // 创建 MethodHandle
            MethodHandle methodHandle = LOOKUP.unreflect(method);

            // 处理静态方法
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

    // 查找方法（支持自动装箱/拆箱和子类匹配）
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        // 先尝试精确匹配
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // 再尝试模糊匹配（支持装箱/拆箱和子类）
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && isMethodCompatible(method, parameterTypes)) {
                    return method;
                }
            }
            throw e;
        }
    }

    /**
     * 通过反射 Method 对象获取方法调用器
     *
     * @param method 反射 Method 对象
     * @return 方法调用器
     */
    public static MethodCaller getCaller(Method method) {
        // 生成唯一的缓存键（类名 + 方法签名）
        String methodSignature = generateMethodSignature(method);
        return METHOD_CALLER_CACHE.computeIfAbsent(method.getDeclaringClass(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodSignature, k -> createMethodCaller(method));
    }

    // 生成方法签名（用于缓存键）
    private static String generateMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(parameterTypes[i].getName());
        }
        return sb.append(")").toString();
    }

    // 基于 Method 对象创建方法调用器
    private static MethodCaller createMethodCaller(Method method) {
        try {
            method.setAccessible(true);
            MethodHandle methodHandle = LOOKUP.unreflect(method);

            // 处理静态方法
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
            throw new RuntimeException("Failed to create method caller for " + method, e);
        }
    }

    // 判断方法是否兼容参数类型
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

    // 判断类型是否兼容（支持装箱/拆箱）
    private static boolean isAssignableFrom(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        // 处理基本类型和包装类型的关系
        if (targetType.isPrimitive()) {
            return boxedType(targetType).isAssignableFrom(sourceType);
        } else if (sourceType.isPrimitive()) {
            return targetType.isAssignableFrom(boxedType(sourceType));
        }

        return false;
    }

    // 获取基本类型的包装类型
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

    // 生成方法签名
    private static String generateMethodSignature(String methodName, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(methodName).append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(parameterTypes[i].getName());
        }
        return sb.append(")").toString();
    }

    // 获取参数类型数组
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