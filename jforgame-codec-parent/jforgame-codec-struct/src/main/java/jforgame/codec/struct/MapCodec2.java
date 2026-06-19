package jforgame.codec.struct;


import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map codec (Key is forced to String type, compatible with json format).
 * Value element can be parent class or abstract class.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.MapCodec
 */
public class MapCodec2 extends Codec {

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
        if (size == 0) {
            return result;
        }

        Class<?> eleType = valueType;
        byte status = ByteBuffUtil.readByte(in);
        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but map value type is not same!");
            }
        }
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
        // Loop to decode key-value pairs (Key is fixed as String)
        for (int i = 0; i < size; i++) {
            // Decode Key (forced to use StringCodec)
            String key = (String) getSerializer(String.class).decode(in, String.class, null);
            // Decode Value (using the specified Value type codec)
            if (status == 1) {
                eleType = messageFactory.getMessage(ByteBuffUtil.readInt(in));
            }
            Codec valueCodec = getSerializer(eleType);
            Object value = valueCodec.decode(in, eleType, null);
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
        if (size == 0) {
            return;
        }
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
//        Key is uniformly string, status is determined by value type
//        1: Basic type, write status 0;
//        2: Not basic type, and element types are the same, and not abstract class (interface), write status 0
//        3: Otherwise, write status 1, then during iteration, also write each element's type id
        byte status = 0;
        if (!TypeUtil.isPrimitiveOrString(wrapper)) {
            Set<Class<?>> elemType = new HashSet<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    elemType.add(value.getClass());
                }
            }
            if (elemType.size() > 1 || Modifier.isAbstract(wrapper.getModifiers()) || Modifier.isInterface(wrapper.getModifiers())) {
                status = 1;
            }
        }

        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but map value type is not same!");
            }
        }
        ByteBuffUtil.writeByte(out, status);

        // Loop to encode key-value pairs (Key is fixed as String)
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class<?> eleType = wrapper;

            if (value == null) {
                throw new IllegalStateException("Map value is null, key: " + key);
            }

            // Encode value
            getSerializer(String.class).encode(out, key, null);
            // Encode Value (get codec according to actual type)
            if (status == 1) {
                eleType = value.getClass();
                int messageId = messageFactory.getMessageId(eleType);
                ByteBuffUtil.writeInt(out, messageId);
            }
            Codec valueCodec = getSerializer(eleType);
            valueCodec.encode(out, value, null);
        }
    }

}
