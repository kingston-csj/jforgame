package jforgame.codec.json;


import jforgame.codec.MessageCodec;
import jforgame.codec.MessageCodecException;
import jforgame.commons.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * JSON-based encoding and decoding method, the simplest and most practical, especially suitable for lightweight game servers like mini games.
 *
 * @since 4.0.0
 */
public class JsonCodec implements MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(JsonCodec.class);

    @Override
    public Object decode(Class<?> clazz, byte[] body) {
        try {
            return JsonUtil.string2Object(new String(body, StandardCharsets.UTF_8), clazz);
        } catch (Exception e) {
            logger.error("json codec decode failed for message {}", clazz.getName(), e);
            throw new MessageCodecException("json codec decode failed for message " + clazz.getName(), e);
        }
    }

    @Override
    public byte[] encode(Object message) {
        try {
            String json = JsonUtil.object2String(message);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MessageCodecException("json codec encode failed for message " + message.getClass().getName(), e);
        }
    }
}

