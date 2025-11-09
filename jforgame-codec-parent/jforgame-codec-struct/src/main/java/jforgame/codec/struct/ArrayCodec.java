package jforgame.codec.struct;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

/**
 * 数组属性序列化
 * 注：由于数组元素bean没有像Message一样注册id，
 * 因此数组的元素不能是父类或抽象类
 * 数组长度不能超过Short.MAX_VALUE，即最多65535
 */
public class ArrayCodec extends Codec {

    @Override
    public Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
        int size = ByteBuffUtil.readShort(in);
        if (size < 0) {
            throw new RuntimeException("Array size less than zero!");
        }
        Object array = ReflectUtil.newArray(type, wrapper, size);

        for (int i = 0; i < size; i++) {
            Codec fieldCodec = Codec.getSerializer(wrapper);
            Object eleValue = fieldCodec.decode(in, wrapper, null);
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
        for (int i = 0; i < size; i++) {
            Object elem = Array.get(value, i);
            Class<?> clazz = elem.getClass();
            Codec fieldCodec = Codec.getSerializer(clazz);
            fieldCodec.encode(out, elem, wrapper);
        }
    }

}
