package jforgame.commons.util;


/**
 * 类型工具类
 */
public class TypeUtil {

    /**
     * 判断类型是否为基本类型或字符串
     *
     * @param clazz 要判断的类型
     * @return 如果是基本类型或字符串则返回true，否则返回false
     */
    public static boolean isPrimitiveOrString(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        // 判断是否为基本类型
        if (clazz.isPrimitive()) {
            return true;
        }
        // 判断是否为基本类型的包装类
        if (isWrapperType(clazz)) {
            return true;
        }
        // 判断是否为字符串类型
        if (clazz == String.class) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为基本类型的包装类
     */
    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Integer.class
                || clazz == Long.class
                || clazz == Short.class
                || clazz == Byte.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Boolean.class
                || clazz == Character.class;
    }


    /**
     * 判断值是否与类型兼容
     * 例如：
     * 1. 类型为 Integer，值为 1 时，返回 true
     * 2. 类型为 Integer，值为 1L 时，返回 true
     * 3. 类型为 Integer，值为 "1" 时，返回 true
     * 4. 类型为 Integer，值为 "1.0" 时，返回 false
     *
     * @param value 值
     * @param type  类型
     * @return 如果值与类型兼容则返回true，否则返回false
     */
    public static boolean isCompatibleType(Object value, Class<?> type) {
        if ((value == null) || (type.isInstance(value))) {
            return true;
        }
        if ((type.equals(Integer.TYPE)) && ((value instanceof Integer))) {
            return true;
        }
        if ((type.equals(Long.TYPE)) && ((value instanceof Long))) {
            return true;
        }
        if ((type.equals(Double.TYPE)) && ((value instanceof Double))) {
            return true;
        }
        if ((type.equals(Float.TYPE)) && ((value instanceof Float))) {
            return true;
        }
        if ((type.equals(Short.TYPE)) && ((value instanceof Short))) {
            return true;
        }
        if ((type.equals(Byte.TYPE)) && ((value instanceof Byte))) {
            return true;
        }
        if ((type.equals(Character.TYPE)) && ((value instanceof Character))) {
            return true;
        }
        if ((type.equals(Boolean.TYPE)) && ((value instanceof Boolean))) {
            return true;
        }
        return false;
    }
}