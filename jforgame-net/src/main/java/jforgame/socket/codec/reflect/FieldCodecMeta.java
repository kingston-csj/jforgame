package jforgame.socket.codec.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class FieldCodecMeta {
	
	private Field field;
	
	private Class<?> type;
	
	private Codec codec;
	/** collection类型里的元素 */
	private Class<?> wrapper;
	
	public static FieldCodecMeta valueOf(Field field, Codec codec) {
		FieldCodecMeta meta = new FieldCodecMeta();
		meta.field = field;
		Class<?> type = field.getType();
		meta.type = type;
		meta.codec = codec;
		
		if (Collection.class.isAssignableFrom(type)) {
			meta.wrapper = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
		} else if (type.isArray()) {
			meta.wrapper = type.getComponentType();
		}
		return meta;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return type;
	}

	public Codec getCodec() {
		return codec;
	}

	public Class<?> getWrapper() {
		return wrapper;
	}

	@Override
	public String toString() {
		return "FieldCodecMeta [field=" + field + ", type=" + type + ", serializer=" + codec + ", wrapper="
				+ wrapper + "]";
	}
	
}
