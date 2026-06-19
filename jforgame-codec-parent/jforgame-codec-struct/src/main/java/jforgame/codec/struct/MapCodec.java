package jforgame.codec.struct;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Map codec (Key is forced to String type, compatible with json format).
 * Value element cannot be parent class or abstract class.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.MapCodec2
 */
public class MapCodec extends Codec {

    @Override
    @SuppressWarnings("all")
    public Object decode(ByteBuffer in, Class<?> type, Class<?> valueType) {
        // Read Map size (short type)
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Map size less than zero!");
        }
        Map<String, Object> result = new HashMap<>(size);
        if (valueType == null) {
            throw new IllegalArgumentException("MapCodec: valueType is null");
        }

        // Value type: prefer the type passed by wrapper, otherwise default to Object
        Class<?> actualValueType = valueType != null ? valueType : Object.class;
        Codec valueCodec = getSerializer(actualValueType);

        // Loop to decode key-value pairs (Key is fixed as String)
        for (int i = 0; i < size; i++) {
            // Decode Key (forced to use StringCodec)
            String key = (String) getSerializer(String.class).decode(in, String.class, null);
            // Decode Value (using the specified Value type codec)
            Object value = valueCodec.decode(in, actualValueType, null);
            result.put(key, value);
        }

        return result;
    }

    @Override
    public void encode(ByteBuffer out, Object target, Class<?> wrapper) {
        if (target == null) {
            // Write empty Map with size=0
            ByteBuffUtil.writeShort(out, (short) 0);
            return;
        }

        Map<String, Object> map = (Map<String, Object>) target;
        int size = map.size();
        if (size > Short.MAX_VALUE) {
            throw new RuntimeException("Map size less than zero or exceed max short value!");
        }
        ByteBuffUtil.writeShort(out, (short) size);

        // Loop to encode key-value pairs (Key is fixed as String)
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // Encode Key
            getSerializer(String.class).encode(out, key, null);
            if (value == null) {
                throw new IllegalStateException("Map value is null, key: " + key);
            }
            if (wrapper == null) {
                throw new IllegalArgumentException("MapCodec: valueType is null");
            }
            if (value.getClass() != wrapper) {
                throw new IllegalStateException("MapCodec only supports strict homogeneous values, value type: " + value.getClass().getName() + ", wrapper: " + wrapper.getName());
            }
            Codec valueCodec = getSerializer(wrapper);
            valueCodec.encode(out, value, null);
        }
    }

}
