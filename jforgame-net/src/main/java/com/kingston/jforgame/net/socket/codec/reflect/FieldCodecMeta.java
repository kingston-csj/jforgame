package com.kingston.jforgame.net.socket.codec.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class FieldCodecMeta {
	
	private Field field;
	
	private Class<?> type;
	
	private Serializer serializer;
	/** collection类型里的元素 */
	private Class<?> wrapper;
	
	public static FieldCodecMeta valueOf(Field field, Serializer serializer) {
		FieldCodecMeta meta = new FieldCodecMeta();
		meta.field = field;
		Class<?> type = field.getType();
		meta.type = type;
		meta.serializer = serializer;
		
		if (Collection.class.isAssignableFrom(type)) {
			meta.wrapper = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
		}
		return meta;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return type;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public Class<?> getWrapper() {
		return wrapper;
	}

	@Override
	public String toString() {
		return "FieldCodecMeta [field=" + field + ", type=" + type + ", serializer=" + serializer + ", wrapper="
				+ wrapper + "]";
	}
	
}
