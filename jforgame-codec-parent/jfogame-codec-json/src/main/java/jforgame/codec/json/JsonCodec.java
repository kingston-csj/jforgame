package jforgame.codec.json;


import jforgame.codec.MessageCodec;
import jforgame.commons.util.JsonUtil;

import java.nio.charset.StandardCharsets;

/**
 * JSON-based encoding and decoding method, the simplest and most practical, especially suitable for lightweight game servers like mini games.
 * @since 4.0.0
 */
public class JsonCodec implements MessageCodec {
    @Override
    public Object decode(Class<?> clazz, byte[] body) {
        return JsonUtil.string2Object(new String(body, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte[] encode(Object message) {
        String json = JsonUtil.object2String(message);
        return json.getBytes(StandardCharsets.UTF_8);
    }
}

