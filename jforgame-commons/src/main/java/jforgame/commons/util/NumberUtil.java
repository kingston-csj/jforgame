package jforgame.commons.util;

/**
 * Universal number and boolean conversion utility.
 * Provide safe conversion from arbitrary object to basic numeric/boolean type,
 * support blank string trimming and compatible with 0/1 numeric boolean notation.
 * All parsing failures and null input will return default value.
 */
public final class NumberUtil {
    private NumberUtil() {
    }

    /**
     * Convert object to boolean, default fallback value is false.
     * Support true/false string and numeric 1/0 format.
     *
     * @param object raw input object
     * @return parsed boolean result
     */
    public static boolean booleanValue(Object object) {
        return booleanValue(object, Boolean.FALSE);
    }

    /**
     * Convert object to boolean value with custom default fallback.
     * Rule:
     * "true" / "1" return true
     * "false" / "0" return false
     * null or unrecognized content returns default value.
     * Automatically trim whitespace and ignore case.
     *
     * @param object       raw input object (Boolean, String, Number etc.)
     * @param defaultValue fallback value for null or parse failure
     * @return parsed boolean result
     */
    public static boolean booleanValue(Object object, boolean defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        String text = object.toString().trim().toLowerCase();
        if ("true".equals(text) || "1".equals(text)) {
            return true;
        }
        if ("false".equals(text) || "0".equals(text)) {
            return false;
        }
        return defaultValue;
    }

    /**
     * Convert object to byte, default fallback value is 0.
     * Automatically trim whitespace, return 0 if parsing failed.
     *
     * @param object raw input object
     * @return parsed byte result
     */
    public static byte byteValue(Object object) {
        return byteValue(object, (byte) 0);
    }

    /**
     * Convert object to byte with custom default fallback.
     * Automatically trim whitespace, return default value if parsing failed.
     *
     * @param object       raw input object
     * @param defaultValue fallback value for null or parse failure
     * @return parsed byte result
     */
    public static byte byteValue(Object object, byte defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(object.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Convert object to short, default fallback value is 0.
     * Automatically trim whitespace, return 0 if parsing failed.
     *
     * @param object raw input object
     * @return parsed short result
     */
    public static short shortValue(Object object) {
        return shortValue(object, (short) 0);
    }

    /**
     * Convert object to short with custom default fallback.
     * Automatically trim whitespace, return default value if parsing failed.
     *
     * @param object       raw input object
     * @param defaultValue fallback value for null or parse failure
     * @return parsed short result
     */
    public static short shortValue(Object object, short defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(object.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Convert object to int, default fallback value is 0.
     * Automatically trim whitespace, return 0 if parsing failed.
     *
     * @param object raw input object
     * @return parsed int result
     */
    public static int intValue(Object object) {
        return intValue(object, 0);
    }

    /**
     * Convert object to int with custom default fallback.
     * Automatically trim whitespace, return default value if parsing failed.
     *
     * @param object       raw input object
     * @param defaultValue fallback value for null or parse failure
     * @return parsed int result
     */
    public static int intValue(Object object, int defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(object.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Convert object to long, default fallback value is 0.
     * Automatically trim whitespace, return 0 if parsing failed.
     *
     * @param object raw input object
     * @return parsed long result
     */
    public static long longValue(Object object) {
        return longValue(object, 0L);
    }

    /**
     * Convert object to long with custom default fallback.
     * Automatically trim whitespace, return default value if parsing failed.
     *
     * @param object       raw input object
     * @param defaultValue fallback value for null or parse failure
     * @return parsed long result
     */
    public static long longValue(Object object, long defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(object.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Convert object to double, default fallback value is 0.0.
     * Automatically trim whitespace, return 0.0 if parsing failed.
     *
     * @param object raw input object
     * @return parsed double result
     */
    public static double doubleValue(Object object) {
        return doubleValue(object, 0D);
    }

    /**
     * Convert object to double with custom default fallback.
     * Automatically trim whitespace, return default value if parsing failed.
     *
     * @param object       raw input object
     * @param defaultValue fallback value for null or parse failure
     * @return parsed double result
     */
    public static double doubleValue(Object object, double defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(object.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}