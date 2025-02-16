package jforgame.orm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

	public static Object getMethodValue(Object obj, String property)
		throws Exception {
		String methodName = "get" + StringUtils.firstLetterToUpperCase(property);
		Method method = obj.getClass().getMethod(methodName);
		return method.invoke(obj);
	}

	/**
	 * 获取一个类上所有的字段包含父类
	 */
	public static Field[] getAllFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (clazz.getSuperclass() != null) {
			Field[] superFields = getAllFields(clazz.getSuperclass());
			if (superFields.length > 0) {
				Field[] newFields = new Field[fields.length + superFields.length];
				System.arraycopy(fields, 0, newFields, 0, fields.length);
				System.arraycopy(superFields, 0, newFields, fields.length, superFields.length);
				fields = newFields;
			}
		}
		return fields;
	}

}
