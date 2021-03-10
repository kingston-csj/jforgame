package jforgame.server.game.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import jforgame.server.db.DbUtils;
import jforgame.server.logs.LoggerUtils;

/**
 * 保存一些系统级别的参数
 * 
 * @author kinson
 */
public class SystemParameters {

	/** 每日重置的时间戳 */
	public static volatile long dailyResetTimestamp;

	public static synchronized void load() throws Exception {
		String sql = "SELECT * FROM `systemrecord`";
		List<Map<String, Object>> datas = DbUtils.queryMapList(DbUtils.DB_USER, sql);

		// 给所有field填值
		for (Map<String, Object> data : datas) {
			String key = (String) data.get("key");
			String value = (String) data.get("value");

			callSetter(key, value);
		}

	}

	private static void callSetter(String filedName, String value) throws Exception {
		Class selfClass = SystemParameters.class;
		Field field = selfClass.getDeclaredField(filedName);
		if (!Modifier.isVolatile(field.getModifiers())) {
			throw new RuntimeException("SystemParameters 属性" + field.getName() + "缺少修饰符volatile");
		}
		Class fieldType = field.getType();
		if (fieldType == String.class) {
			field.set(selfClass, value);
		} else if (fieldType == Byte.class || fieldType == byte.class) {
			field.set(selfClass, Byte.valueOf(value));
		} else if (fieldType == Short.class || fieldType == short.class) {
			field.set(selfClass, Short.parseShort(value));
		} else if (fieldType == Integer.class || fieldType == int.class) {
			field.set(selfClass, Integer.parseInt(value));
		} else if (fieldType == Long.class || fieldType == long.class) {
			field.set(selfClass, Long.parseLong(value));
		} else if (fieldType == Float.class || fieldType == float.class) {
			field.set(selfClass, Float.valueOf(value));
		} else if (fieldType == Double.class || fieldType == double.class) {
			field.set(selfClass, Double.parseDouble(value));
		}
	}

	private static synchronized void setFieldValue(String key, Object value) {
		try {
			Class selfClass = SystemParameters.class;
			Field field = selfClass.getField(key);
			if (field != null) {
				field.set(selfClass, value);
			}

		} catch (Exception e) {
			LoggerUtils.error("dbutil setValue error", e);
		}
	}

	private static synchronized void saveToDb(String key, String value) {
		// 入库
		String sql = "UPDATE `systemrecord` SET `value`='" + value + "' WHERE `key`='" + key + "'";
		try {
			DbUtils.executeUpdate(sql);
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public static synchronized void update(String key, byte value) {
		setFieldValue(key, value);
		saveToDb(key, Byte.toString(value));
	}

	public static synchronized void update(String key, short value) {
		setFieldValue(key, value);
		saveToDb(key, Short.toString(value));
	}

	public static synchronized void update(String key, int value) {
		setFieldValue(key, value);
		saveToDb(key, Integer.toString(value));
	}

	public static synchronized void update(String key, long value) {
		setFieldValue(key, value);
		saveToDb(key, Long.toString(value));
	}

	public static synchronized void update(String key, float value) {
		setFieldValue(key, value);
		saveToDb(key, Float.toString(value));
	}

	public static synchronized void update(String key, double value) {
		setFieldValue(key, value);
		saveToDb(key, Double.toString(value));
	}

	public static synchronized void update(String key, String value) {
		setFieldValue(key, value);
		saveToDb(key, value);
	}

}
