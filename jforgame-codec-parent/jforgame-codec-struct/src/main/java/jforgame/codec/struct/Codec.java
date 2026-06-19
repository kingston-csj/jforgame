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
 * Base class for message encoding and decoding, also manages all type field codecs.
 * Besides basic type encoding/decoding, also supports collection type encoding/decoding, Map type is not supported for now.
 * This tool uses fixed-length encoding for integers, not variable-length encoding.
 * If business has special needs, you can implement variable-length encoding for integers yourself and replace it through {@link #replace(Class, Codec)} interface.
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
        register(Object[].class, new ArrayCodec());
        register(Map.class, new MapCodec());
    }

    /**
     * binding clazz and codec
     * if clazz repeated, an IllegalStateException exception thrown
     *
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
     *
     * @param clazz class of the message
     * @param codec codec of the message
     */
    public static void replace(Class<?> clazz, Codec codec) {
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
            for (Field field : fields) {
                int modifier = field.getModifiers();
                if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
                    continue;
                }
                // Ignore server-only fields
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
     * Message decoding
     *
     * @param in      buffer to read
     * @param type    class type
     * @param wrapper collection element wrapper class
     * @return request message
     */
    public abstract Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper);


    /**
     * Message encoding
     *
     * @param out     buffer to write
     * @param value   message object
     * @param wrapper collection element wrapper class
     */
    public abstract void encode(ByteBuffer out, Object value, Class<?> wrapper);

}
