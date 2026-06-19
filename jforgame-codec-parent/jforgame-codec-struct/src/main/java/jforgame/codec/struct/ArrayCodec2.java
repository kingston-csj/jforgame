package jforgame.codec.struct;


import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Array property serialization.
 * Array elements can be parent class or abstract class.
 * Array length cannot exceed Short.MAX_VALUE, which is 65535 at most.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.ArrayCodec
 */
public class ArrayCodec2 extends Codec {

    @Override
    public Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Array size less than zero!");
        }
        Object array = ReflectUtil.newArray(type, wrapper, size);
        if (size == 0) {
            return array;
        }
        // Element type status: 0 means basic type or same element type, 1 means element types are different
        byte status = ByteBuffUtil.readByte(in);

        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but array element type is not same!");
            }
        }
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;

        for (int i = 0; i < size; i++) {
            Class<?> eleType = wrapper;
            if (status == 1) {
                int messageId = ByteBuffUtil.readInt(in);
                eleType = messageFactory.getMessage(messageId);
            }
            Codec fieldCodec = Codec.getSerializer(eleType);
            Object eleValue = fieldCodec.decode(in, eleType, null);
            Array.set(array, i, eleValue);
        }

        return array;
    }

    @Override
    public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
        if (value == null) {
            ByteBuffUtil.writeShort(out, (short) 0);
            return;
        }
        int size = Array.getLength(value);
        if (size > Short.MAX_VALUE) {
            throw new RuntimeException("Collection size less than zero or exceed max short value!");
        }
        ByteBuffUtil.writeShort(out, (short) size);

        if (size == 0) {
            return;
        }
//        1: Basic type, write status 0;
//        2: Not basic type, and element types are the same, and not abstract class (interface), write status 0
//        3: Otherwise, write status 1, then during iteration, also write each element's type id
        byte status = 0;
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
        // Basic type, write status code: 0
        if (!TypeUtil.isPrimitiveOrString(wrapper)) {
            Set<Class<?>> elemType = new HashSet<>();
            for (int i = 0; i < size; i++) {
                Object elem = Array.get(value, i);
                if (elem == null) {
                    throw new IllegalStateException("Array element is null");
                }
                Class<?> clazz = elem.getClass();
                elemType.add(clazz);
            }
            // Element types are inconsistent, write status code: 1
            if (elemType.size() > 1 || Modifier.isAbstract(wrapper.getModifiers()) || Modifier.isInterface(wrapper.getModifiers())) {
                status = (byte) 1;
            }
        }

        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but array element type is not same!");
            }
        }

        ByteBuffUtil.writeByte(out, status);

        for (int i = 0; i < size; i++) {
            Object elem = Array.get(value, i);
            if (elem == null) {
                throw new IllegalStateException("Array element is null");
            }
            Class<?> clazz = elem.getClass();
            Class<?> eleType = wrapper;
            if (status == 1) {
                eleType = elem.getClass();
                int messageId = messageFactory.getMessageId(eleType);
                ByteBuffUtil.writeInt(out, messageId);
            }
            Codec fieldCodec = Codec.getSerializer(clazz);
            fieldCodec.encode(out, elem, eleType);
        }
    }

}
