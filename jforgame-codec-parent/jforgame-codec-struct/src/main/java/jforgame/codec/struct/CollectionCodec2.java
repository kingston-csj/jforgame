package jforgame.codec.struct;

import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection property serialization.
 * Collection elements can be parent class or abstract class.
 * Collection length cannot exceed Short.MAX_VALUE, which is 65535 at most.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.CollectionCodec
 */
public class CollectionCodec2 extends Codec {

    @Override
    @SuppressWarnings("all")
    public Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Collection size less than zero!");
        }
        int modifier = type.getModifiers();
        Collection<Object> result = null;

        if (Modifier.isAbstract(modifier) || Modifier.isInterface(modifier)) {
            if (List.class.isAssignableFrom(type)) {
                result = new ArrayList<>();
            } else if (Set.class.isAssignableFrom(type)) {
                result = new HashSet<>();
            }
        } else {
            try {
                result = (Collection) type.newInstance();
            } catch (Exception e) {
                result = new ArrayList<>();
            }
        }
        if (size == 0) {
            return result;
        }
        // Element type status: 0 means basic type or same element type, 1 means element types are different
        byte status = ByteBuffUtil.readByte(in);
        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but collection element type is not same!");
            }
        }
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
        for (int i = 0; i < size; i++) {
            Class<?> elementType = wrapper;
            if (status == 1) {
                int messageId = ByteBuffUtil.readInt(in);
                elementType = messageFactory.getMessage(messageId);
            }

            Codec fieldCodec = getSerializer(elementType);
            Object eleValue = fieldCodec.decode(in, elementType, null);
            result.add(eleValue);
        }

        return result;
    }

    @Override
    public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
        if (value == null) {
            ByteBuffUtil.writeShort(out, (short) 0);
            return;
        }
        Collection<Object> collection = (Collection) value;
        int size = collection.size();
        ByteBuffUtil.writeShort(out, (short) size);
        if (size == 0) {
            return;
        }
        if (size > Short.MAX_VALUE) {
            throw new RuntimeException("Collection size less than zero or exceed max short value!");
        }
        byte status = 0;
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
        // Basic type, write status code: 0
        // Collection element types are consistent, write status code: 1
        if (!TypeUtil.isPrimitiveOrString(wrapper)) {
            Set<Class<?>> elemType = new HashSet<>();
            for (Object elem : collection) {
                if (elem == null) {
                    throw new IllegalStateException("Collection element is null");
                }
                Class<?> clazz = elem.getClass();
                elemType.add(clazz);
            }
            // Collection element types are inconsistent
            if (elemType.size() > 1 || Modifier.isAbstract(wrapper.getModifiers()) || Modifier.isInterface(wrapper.getModifiers())) {
                status = (byte) 1;
            }
        }
        if (StructCodecEnvironment.collectionSerializeMode == CollectionSerializeMode.STRICT_HOMOGENEOUS) {
            if (status == 1) {
                throw new IllegalStateException("collectionSerializeMode is STRICT_HOMOGENEOUS, but collection element type is not same!");
            }
        }

        ByteBuffUtil.writeByte(out, status);

        for (Object elem : collection) {
            if (elem == null) {
                throw new IllegalStateException("Collection element is null");
            }
            Class<?> eleType = wrapper;
            if (status == 1) {
                eleType = elem.getClass();
                int messageId = messageFactory.getMessageId(eleType);
                ByteBuffUtil.writeInt(out, messageId);
            }
            Codec fieldCodec = getSerializer(eleType);
            fieldCodec.encode(out, elem, eleType);
        }
    }

}
