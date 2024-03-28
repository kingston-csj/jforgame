package jforgame.commons;

/**
 * This utils is used for number convert, you can convert number typed string to target number type
 * @author kinson
 */
public final class NumberUtil {


    private NumberUtil() {

    }

    public static boolean booleanValue(Object object) {
        return booleanValue(object, Boolean.FALSE);
    }

    public static boolean booleanValue(Object object, boolean defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Boolean.class) {
            return (boolean) object;
        }
        try {
            return Boolean.parseBoolean(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static byte byteValue(Object object) {
        return byteValue(object, (byte)0);
    }

    public static byte byteValue(Object object, byte defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Byte.class) {
            return (byte) object;
        }
        try {
            return Byte.parseByte(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static short shortValue(Object object) {
        return shortValue(object, (short) 0);
    }

    public static short shortValue(Object object, short defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Short.class) {
            return (short) object;
        }
        try {
            return Short.parseShort(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int intValue(Object object) {
        return intValue(object, 0);
    }

    public static int intValue(Object object, int defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Integer.class) {
            return (int) object;
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long longValue(Object object) {
        return longValue(object, 0L);
    }

    public static long longValue(Object object, long defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Long.class) {
            return (long) object;
        }
        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double doubleValue(Object object) {
        return doubleValue(object, 0);
    }

    public static double doubleValue(Object object, double defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object.getClass() == Double.class) {
            return (long) object;
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
