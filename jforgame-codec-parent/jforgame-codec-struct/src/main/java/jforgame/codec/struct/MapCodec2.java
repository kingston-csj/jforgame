package jforgame.codec.struct;


import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map codec with homogeneous Integer or String keys.
 * Value element can be parent class or abstract class.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.MapCodec
 */
public class MapCodec2 extends Codec {

    @Override
    public Object decode(ByteBuffer in, Class<?> type, Class<?>[] wrapper) {
        MapCodec.validateWrapper(wrapper);
        Class<?> keyType = wrapper[0];
        Class<?> valueType = wrapper[1];
        // Read Map size (short type)
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Map size less than zero!");
        }
        Map<Object, Object> result = new HashMap<>(size);
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
        Codec keyCodec = getSerializer(keyType);
        // Decode keys according to the declared generic type.
        for (int i = 0; i < size; i++) {
            Object key = keyCodec.decode(in, keyType, null);
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
    public void encode(ByteBuffer out, Object target, Class<?>[] wrapper) {
        MapCodec.validateWrapper(wrapper);
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
        if (size == 0) {
            return;
        }
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
//        Status is determined by value type
//        1: Basic type, write status 0;
//        2: Not basic type, and element types are the same, and not abstract class (interface), write status 0
//        3: Otherwise, write status 1, then during iteration, also write each element's type id
        byte status = 0;
        if (!TypeUtil.isPrimitiveOrString(valueType)) {
            Set<Class<?>> elemType = new HashSet<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    elemType.add(value.getClass());
                }
            }
            if (elemType.size() > 1 || Modifier.isAbstract(valueType.getModifiers()) || Modifier.isInterface(valueType.getModifiers())) {
                status = 1;
            }
        }

        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but map value type is not same!");
            }
        }
        ByteBuffUtil.writeByte(out, status);

        Codec keyCodec = getSerializer(keyType);
        // Encode keys according to the declared generic type.
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Class<?> eleType = valueType;

            if (value == null) {
                throw new IllegalStateException("Map value is null, key: " + key);
            }

            // Encode Key
            keyCodec.encode(out, key, null);
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
