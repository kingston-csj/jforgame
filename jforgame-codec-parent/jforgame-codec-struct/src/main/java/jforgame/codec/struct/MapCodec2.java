package jforgame.codec.struct;


import jforgame.commons.util.TypeUtil;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map 编解码器（Key 强制为 String 类型，兼容json格式）
 * 元素Value可以是父类或抽象类
 *
 * @see jforgame.codec.struct.CollectionSerializeMode
 * @see jforgame.codec.struct.MapCodec
 */
public class MapCodec2 extends Codec {

    @Override
    @SuppressWarnings("all")
    public Object decode(ByteBuffer in, Class<?> type, Class<?> valueType) {
        // 读取 Map 大小（short 类型）
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
        // 循环解码键值对（Key 固定为 String）
        for (int i = 0; i < size; i++) {
            // 解码 Key（强制用 StringCodec）
            String key = (String) getSerializer(String.class).decode(in, String.class, null);
            // 解码 Value（用指定的 Value 类型编解码器）
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
            // 空 Map 写入 size=0
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
//        key统一为string，根据value类型判断状态
//        1:基本类型，写入状态0;
//        2:不是基本类型，且元素类型是一样的，且不是抽象类(接口)写入状态0
//        3:否则，写入状态1，然后在迭代的时候，同时写入每个元素的类型id
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

        // 循环编码键值对（Key 强制为 String）
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class<?> eleType = wrapper;

            if (value == null) {
                throw new IllegalStateException("Map value is null, key: " + key);
            }

            // 编码 value
            getSerializer(String.class).encode(out, key, null);
            // 编码 Value（根据实际类型获取编解码器）
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
