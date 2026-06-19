package jforgame.commons.util;


/**
 * Type utility class
 */
public class TypeUtil {

    /**
     * Checks if the type is a primitive type or String
     *
     * @param clazz the type to check
     * @return true if it is a primitive type or String, otherwise false
     */
    public static boolean isPrimitiveOrString(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        // Check if it is a primitive type
        if (clazz.isPrimitive()) {
            return true;
        }
        // Check if it is a wrapper type of primitive type
        if (isWrapperType(clazz)) {
            return true;
        }
        // Check if it is a String type
        if (clazz == String.class) {
            return true;
        }
        return false;
    }

    /**
     * Checks if it is a wrapper type of primitive type
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
     * Checks if a value is compatible with a type
     * Examples:
     * 1. Type is Integer, value is 1, returns true
     * 2. Type is Integer, value is 1L, returns true
     * 3. Type is Integer, value is "1", returns true
     * 4. Type is Integer, value is "1.0", returns false
     *
     * @param value the value
     * @param type  the type
     * @return true if the value is compatible with the type, otherwise false
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