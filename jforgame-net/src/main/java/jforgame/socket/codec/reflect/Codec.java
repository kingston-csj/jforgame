package jforgame.socket.codec.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Codec {

	private static Map<Class<?>, Codec> class2Serializers = new ConcurrentHashMap<>();

	static {
		register(Boolean.TYPE, new BooleanCodec());
		register(Boolean.class, new BooleanCodec());
		register(Byte.TYPE, new ByteCodec());
		register(Byte.class, new ByteCodec());
		register(Short.TYPE, new ShortCodec());
		register(Short.class, new ShortCodec());
		register(Integer.TYPE, new IntCodec());
		register(Integer.class, new IntCodec());
		register(Float.TYPE, new FloatCodec());
		register(Float.class, new FloatCodec());
		register(Double.TYPE, new DoubleCodec());
		register(Double.class, new DoubleCodec());
		register(Long.TYPE, new LongCodec());
		register(Long.class, new LongCodec());
		register(String.class, new StringCodec());
		register(List.class, new CollectionCodec());
		register(Set.class, new CollectionCodec());
		register(Object[].class, new ArrayCodec());
	}

	public static void register(Class<?> clazz, Codec codec) {
		class2Serializers.put(clazz, codec);
	}

	public static Codec getSerializer(Class<?> clazz) {
		if (class2Serializers.containsKey(clazz)) {
			return class2Serializers.get(clazz);
		}
		if (clazz.isArray()) {
			return class2Serializers.get(Object[].class);
		}
		Field[] fields = clazz.getDeclaredFields();
		LinkedHashMap<Field, Class<?>> sortedFields = new LinkedHashMap<>();
		List<FieldCodecMeta> fieldsMeta = new ArrayList<>();
		for (Field field: fields) {
			int modifier = field.getModifiers();
			if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
				continue;
			}
			field.setAccessible(true);
			sortedFields.put(field, field.getType());
			Class<?> type = field.getType();
			Codec codec = Codec.getSerializer(type);

			fieldsMeta.add(FieldCodecMeta.valueOf(field, codec));
		}
		Codec messageCodec = MessageCodec.valueOf(fieldsMeta);
		Codec.register(clazz, messageCodec);
		return messageCodec;
	}

	/**
	 * 消息解码
	 * @param in
	 * @param type
	 * @param wrapper 集合元素包装类
	 * @return
	 */
	public abstract Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper);

	
	/**
	 * 消息编码
	 * @param out
	 * @param value
	 * @param wrapper 集合元素包装类
	 */
	public abstract void encode(ByteBuffer out, Object value, Class<?> wrapper);

}
