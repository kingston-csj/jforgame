package com.kingston.jforgame.common.utils;

public final class NumberUtil {
	
	public static byte byteValue(Object object) {
		return byteValue(object, Byte.valueOf("0"));
	}
	
	public static byte byteValue(Object object, byte defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		if (object.getClass() == float.class || object.getClass() == Float.class) {
			return (byte)object;
		}
		try {
			return Byte.valueOf(object.toString());
		}catch(Exception e) {
			return defaultValue;
		}
	}

}
 