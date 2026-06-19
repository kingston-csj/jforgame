package jforgame.codec.struct;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

/**
 * Array property serialization.
 * Note: Since array element beans are not registered with an id like Message,
 * array elements cannot be parent class or abstract class.
 * Array length cannot exceed Short.MAX_VALUE, which is 65535 at most.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.ArrayCodec2
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
            if (elem == null) {
                throw new IllegalStateException("Array element is null");
            }
            if (elem.getClass() != wrapper) {
                throw new IllegalStateException("ArrayCodec only supports strict homogeneous elements, element type: " + elem.getClass().getName() + ", wrapper: " + wrapper.getName());
            }
            Class<?> clazz = elem.getClass();
            Codec fieldCodec = Codec.getSerializer(clazz);
            fieldCodec.encode(out, elem, wrapper);
        }
    }

}
