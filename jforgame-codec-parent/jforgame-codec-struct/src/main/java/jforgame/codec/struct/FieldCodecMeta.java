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
     * collection类型里的元素
     */
    private Class<?> wrapper;

    public static FieldCodecMeta valueOf(Field field, Codec codec) {
        FieldCodecMeta meta = new FieldCodecMeta();
        meta.field = field;
        Class<?> type = field.getType();
        meta.type = type;
        meta.codec = codec;
        // 处理 Collection 类型（存储元素类型）
        if (Collection.class.isAssignableFrom(type)) {
            meta.wrapper = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } else if (type.isArray()) {
            // 处理数组类型（存储元素类型）
            meta.wrapper = type.getComponentType();
        }  // 处理 Map 类型（Key 固定为 String，只存储 Value 类型）
        else if (Map.class.isAssignableFrom(type)) {
            // 泛型参数：[0]是Key（忽略，强制为String，兼容json格式），[1]是Value类型
            Type[] actualTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            meta.wrapper = (Class<?>) actualTypes[1]; // 只存 Value 类型
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
