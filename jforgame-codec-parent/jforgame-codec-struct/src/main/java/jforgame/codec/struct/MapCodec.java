package jforgame.codec.struct;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Map 编解码器（Key 强制为 String 类型，后续大版本升级再做改造）
 */
public class MapCodec extends Codec {

    @Override
    @SuppressWarnings("all")
    public Object decode(ByteBuffer in, Class<?> type, Class<?> valueType) {
        // 读取 Map 大小（short 类型）
        int size = ByteBuffUtil.readShort(in);
        Map<String, Object> result = new HashMap<>(size);
        if (valueType == null) {
            throw new IllegalArgumentException("MapCodec: valueType is null");
        }

        // Value 类型：优先用 wrapper 传入的类型，否则默认 Object
        Class<?> actualValueType = valueType != null ? valueType : Object.class;
        Codec valueCodec = getSerializer(actualValueType);

        // 循环解码键值对（Key 固定为 String）
        for (int i = 0; i < size; i++) {
            // 解码 Key（强制用 StringCodec）
            String key = (String) getSerializer(String.class).decode(in, String.class, null);
            // 解码 Value（用指定的 Value 类型编解码器）
            Object value = valueCodec.decode(in, actualValueType, null);
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
        ByteBuffUtil.writeShort(out, (short) size);

        // 循环编码键值对（Key 强制为 String）
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 编码 Key
            getSerializer(String.class).encode(out, key, null);

            // 编码 Value（根据实际类型获取编解码器）
            Class<?> valueType = value != null ? value.getClass() : Object.class;
            Codec valueCodec = getSerializer(valueType);
            valueCodec.encode(out, value, null);
        }
    }

}