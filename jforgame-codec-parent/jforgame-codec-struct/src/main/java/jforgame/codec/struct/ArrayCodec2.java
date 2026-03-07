package jforgame.codec.struct;


import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * 数组属性序列化
 * 数组的元素可以是父类或抽象类
 * 数组长度不能超过Short.MAX_VALUE，即最多65535
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
        // 元素类型状态： 0代表基本类型或者元素类型一致，1代表元素类型不一致
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
//        1:基本类型，写入状态0;
//        2:不是基本类型，且元素类型是一样的，且不是抽象类(接口)写入状态0
//        3:否则，写入状态1，然后在迭代的时候，同时写入每个元素的类型id
        byte status = 0;
        LiteMessageFactory messageFactory = StructCodecEnvironment.messageFactory;
        // 基本类型，写入状态码：0
        if (!TypeUtil.isPrimitiveOrString(wrapper)) {
            Set<Class<?>> elemType = new HashSet<>();
            for (int i = 0; i < size; i++) {
                Class<?> clazz = Array.get(value, i).getClass();
                elemType.add(clazz);
            }
            // 集合元素类型不一致，写入状态码：1
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
