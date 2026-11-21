package jforgame.codec.struct;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class FieldCodecMeta {

    private Field field;

    private Class<?> type;

    private Codec codec;

    /**
     * Since Java has fake generics, need to store collection element types.
     * If it's collection/list, the first element of the array is the collection element type.
     * If it's map, the first element is the key type and the second is the value type.
     */
    private Class<?>[] wrapper;

    public static FieldCodecMeta valueOf(Field field, Codec codec) {
        FieldCodecMeta meta = new FieldCodecMeta();
        meta.field = field;
        Class<?> type = field.getType();
        meta.type = type;
        meta.codec = codec;
        meta.wrapper = new Class[2];
        // Handle Collection type (store element type)
        if (Collection.class.isAssignableFrom(type)) {
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new IllegalStateException("Collection field generic type is missing: " + field);
            }
            Type arg = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            meta.wrapper[0] = extractClass(arg, field);
        } else if (type.isArray()) {
            // Handle array type (store element type)
            meta.wrapper[0] = type.getComponentType();
        }  // Handle Map type (Integer or String keys)
        else if (Map.class.isAssignableFrom(type)) {
            // Generic parameters: [0] is Key, [1] is Value type
            Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new IllegalStateException("Map field generic type is missing: " + field);
            }
            Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
            meta.wrapper[0] = extractClass(actualTypes[0], field); // key type
            MapCodec.validateKeyType(meta.wrapper[0]);
            meta.wrapper[1] = extractClass(actualTypes[1], field); // value type
        }
        return meta;
    }

    private static Class<?> extractClass(Type type, Field field) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof WildcardType ) {
            WildcardType wildcard = (WildcardType) type;
            Type[] upperBounds = wildcard.getUpperBounds();
            // Kotlin covariant lists expose '? extends T'; T is the declared wire type.
            // An unbounded or lower-bounded wildcard cannot identify a concrete wire type.
            if (wildcard.getLowerBounds().length == 0 && upperBounds.length == 1
                    && upperBounds[0] != Object.class) {
                return extractClass(upperBounds[0], field);
            }
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

    public Class<?>[] getWrapper() {
        return wrapper;
    }

    @Override
    public String toString() {
        return "FieldCodecMeta [field=" + field + ", type=" + type + ", serializer=" + codec + ", wrapper="
                + Arrays.toString(wrapper) + "]";
    }

}
