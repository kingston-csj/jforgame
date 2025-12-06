package jforgame.codec.struct;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息体编码解码基类，同时管理所有类型的字段编解码器
 * 除了基础类型的编码解码，还支持集合类型的编码解码，暂不支持Map类型
 * 本工具对于整数的编解码，并没有使用不定长编码，而是使用固定长度编码
 * 如果业务有特殊需要，可以自行实现整数的不定长编解码方式，并通过{@link #replace(Class, Codec)}接口替换。
 */
public abstract class Codec {

	private static final Map<Class<?>, Codec> class2Serializers = new ConcurrentHashMap<>();

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
		register(Map.class, new MapCodec());
		register(Object[].class, new ArrayCodec());
	}

	/**
	 * binding clazz and codec
	 * if clazz repeated, an IllegalStateException exception thrown
	 * @param clazz class of the message
	 * @param codec codec of the message
	 */
	public static void register(Class<?> clazz, Codec codec) {
		if (class2Serializers.containsKey(clazz)) {
			throw new IllegalStateException(clazz.getName() + " duplicated");
		}
		class2Serializers.put(clazz, codec);
	}

	/**
	 * rebinding clazz and codec
	 * you can use this api to replace a relation between clazz and codec
	 * when you want to use a compress version of IntCodec or other types
	 * @param clazz class of the message
	 * @param codec codec of the message
	 */
	public static void replace(Class<?> clazz, Codec codec) {
		if (class2Serializers.containsKey(clazz)) {
			throw new IllegalStateException(clazz.getName() + " duplicated");
		}
		class2Serializers.put(clazz, codec);
	}

	public static Codec getSerializer(Class<?> clazz) {
		if (class2Serializers.containsKey(clazz)) {
			return class2Serializers.get(clazz);
		}
		if (clazz.isArray()) {
			return class2Serializers.get(Object[].class);
		}
		Class<?> currClazz = clazz;

		List<FieldCodecMeta> fieldsMeta = new ArrayList<>();
		while (currClazz != Object.class) {
			Field[] fields = currClazz.getDeclaredFields();
			for (Field field: fields) {
				int modifier = field.getModifiers();
				if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
					continue;
				}
				// 忽略服务器字段
				if (field.isAnnotationPresent(FieldIgnore.class)) {
					continue;
				}
				field.setAccessible(true);
				Class<?> type = field.getType();
				Codec codec = Codec.getSerializer(type);

				fieldsMeta.add(FieldCodecMeta.valueOf(field, codec));
			}
			currClazz = currClazz.getSuperclass();
		}

		Codec messageCodec = BeanCodec.valueOf(fieldsMeta);
		Codec.register(clazz, messageCodec);
		return messageCodec;
	}

	/**
	 * 消息解码
	 * @param in buff to read
	 * @param type class type
	 * @param wrapper 集合元素包装类
	 * @return request message
	 */
	public abstract Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper);


	/**
	 * 消息编码
	 * @param out buff to write
	 * @param value message object
	 * @param wrapper 集合元素包装类
	 */
	public abstract void encode(ByteBuffer out, Object value, Class<?> wrapper);

}
