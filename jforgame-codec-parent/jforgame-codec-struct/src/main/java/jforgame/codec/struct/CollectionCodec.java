package jforgame.codec.struct;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection property serialization.
 * Note: Since collection element beans are not registered with an id like Message,
 * collection elements cannot be parent class or abstract class.
 * Collection length cannot exceed Short.MAX_VALUE, which is 65535 at most.
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.CollectionCodec
 */
public class CollectionCodec extends Codec {

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
                result = (Collection) type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                result = new ArrayList<>();
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }

        for (int i = 0; i < size; i++) {
            Codec fieldCodec = getSerializer(wrapper);
            Object eleValue = fieldCodec.decode(in, wrapper, null);
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
        if (size > Short.MAX_VALUE) {
            throw new RuntimeException("Collection size less than zero or exceed max short value!");
        }
        ByteBuffUtil.writeShort(out, (short) size);

        for (Object elem : collection) {
            if (elem == null) {
                throw new IllegalStateException("Collection element is null");
            }
            if (elem.getClass() != wrapper) {
                throw new IllegalStateException("CollectionCodec only supports strict homogeneous elements, element type: " + elem.getClass().getName() + ", wrapper: " + wrapper.getName());
            }
            Class<?> clazz = elem.getClass();
            Codec fieldCodec = getSerializer(clazz);
            fieldCodec.encode(out, elem, wrapper);
        }
    }

}
