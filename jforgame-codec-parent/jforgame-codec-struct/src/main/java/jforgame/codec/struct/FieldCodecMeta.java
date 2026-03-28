package jforgame.codec.struct;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class FieldCodecMeta {

    private Field field;

    private Class<?> type;

    private Codec codec;
    /**
     * 由于java是假泛型，需要存储集合元素的类型
     * 如果是collection/list，数组第一个为集合元素的类型
     * 如果是map，数组第一个为字典key类型，第二个为字典value类型
     */
    private Class<?>[] wrapper;

    public static FieldCodecMeta valueOf(Field field, Codec codec) {
        FieldCodecMeta meta = new FieldCodecMeta();
        meta.field = field;
        Class<?> type = field.getType();
        meta.type = type;
        meta.codec = codec;
        meta.wrapper = new Class[2];
        // 处理 Collection 类型（存储元素类型）
        if (Collection.class.isAssignableFrom(type)) {
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new IllegalStateException("Collection field generic type is missing: " + field);
            }
            Type arg = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            meta.wrapper[0] = extractClass(arg, field);
        } else if (type.isArray()) {
            // 处理数组类型（存储元素类型）
            meta.wrapper[0] = type.getComponentType();
        }  // 处理 Map 类型（Key 固定为 String，只存储 Value 类型）
        else if (Map.class.isAssignableFrom(type)) {
            // 泛型参数：[0]是Key（忽略，强制为String，兼容json格式），[1]是Value类型
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new IllegalStateException("Map field generic type is missing: " + field);
            }
            Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
//            meta.wrapper[0] = extractClass(actualTypes[0], field); // key 类型
            meta.wrapper[0] = extractClass(actualTypes[1], field); // value 类型
        }
        return meta;
    }

    private static Class<?> extractClass(Type type, Field field) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        }
        throw new IllegalStateException("Unsupported generic type: " + type + ", field: " + field);
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
        return wrapper[0];
    }

    @Override
    public String toString() {
        return "FieldCodecMeta [field=" + field + ", type=" + type + ", serializer=" + codec + ", wrapper="
                + wrapper + "]";
    }

}
