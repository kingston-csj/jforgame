package jforgame.codec.json;


import jforgame.codec.MessageCodec;
import jforgame.commons.util.JsonUtil;

import java.nio.charset.StandardCharsets;

/**
 * 基于json的编解码方式，最为简单实用，特别实用于小游戏等清轻量级游戏服务器
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

