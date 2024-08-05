package jforgame.demo.socket;

import jforgame.codec.MessageCodec;
import jforgame.commons.JsonUtil;

import java.nio.charset.StandardCharsets;

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
