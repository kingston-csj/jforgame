package jforgame.codec.struct;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Map codec with homogeneous Integer or String keys.
 * Value element cannot be parent class or abstract class.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.MapCodec2
 */
public class MapCodec extends Codec {

    static void validateWrapper(Class<?>[] wrapper) {
        if (wrapper == null || wrapper.length < 2 || wrapper[1] == null) {
            throw new IllegalArgumentException("Map wrapper must contain key and value types");
        }
        validateKeyType(wrapper[0]);
    }

    static void validateKeyType(Class<?> keyType) {
        if (keyType != String.class && keyType != Integer.class && keyType != int.class) {
            throw new IllegalArgumentException("Map key type must be Integer or String: " + keyType);
        }
    }

    static void validateKeys(Map<?, ?> map, Class<?> keyType) {
        Class<?> boxedKeyType = keyType == int.class ? Integer.class : keyType;
        for (Object key : map.keySet()) {
            if (key == null || key.getClass() != boxedKeyType) {
                throw new IllegalArgumentException("Map keys must be non-null and match " + boxedKeyType.getName());
            }
        }
    }

    @Override
    public Object decode(ByteBuffer in, Class<?> type, Class<?>[] wrapper) {
        validateWrapper(wrapper);
        Class<?> keyType = wrapper[0];
        Class<?> valueType = wrapper[1];
        // Read Map size (short type)
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Map size less than zero!");
        }
        Map<Object, Object> result = new HashMap<>(size);
        Codec valueCodec = getSerializer(valueType);

        Codec keyCodec = getSerializer(keyType);
        // Decode keys according to the declared generic type.
        for (int i = 0; i < size; i++) {
            Object key = keyCodec.decode(in, keyType, null);
            // Decode Value (using the specified Value type codec)
            Object value = valueCodec.decode(in, valueType, null);
            result.put(key, value);
        }

        return result;
    }

    @Override
    public void encode(ByteBuffer out, Object target, Class<?>[] wrapper) {
        validateWrapper(wrapper);
        Class<?> keyType = wrapper[0];
        Class<?> valueType = wrapper[1];
        if (target == null) {
            // Write empty Map with size=0
            ByteBuffUtil.writeShort(out, (short) 0);
            return;
        }

        Map<?, ?> map = (Map<?, ?>) target;
        MapCodec.validateKeys(map, keyType);
        int size = map.size();
        if (size > Short.MAX_VALUE) {
            throw new RuntimeException("Map size less than zero or exceed max short value!");
        }
        ByteBuffUtil.writeShort(out, (short) size);

        Codec keyCodec = getSerializer(keyType);
        // Encode keys according to the declared generic type.
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            // Encode Key
            keyCodec.encode(out, key, null);
            if (value == null) {
                throw new IllegalStateException("Map value is null, key: " + key);
            }
            if (value.getClass() != valueType) {
                throw new IllegalStateException("MapCodec only supports strict homogeneous values, value type: " + value.getClass().getName() + ", wrapper: " + valueType.getName());
            }
            Codec valueCodec = getSerializer(valueType);
            valueCodec.encode(out, value, null);
        }
    }

}
