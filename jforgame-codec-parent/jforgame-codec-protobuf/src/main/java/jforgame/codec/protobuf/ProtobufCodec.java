package jforgame.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.codec.MessageCodec;
import jforgame.codec.MessageCodecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protobuf message encoder and decoder.
 * Using this codec, server and client communicate through protobuf.
 * For a message class, the server only needs to add {@link ProtobufClass} annotation on the message class.
 * The server can generate the corresponding .proto file through {@link ProtobufIDLGenerator}.
 */
public class ProtobufCodec implements MessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufCodec.class);

    @Override
    public Object decode(Class<?> clazz, byte[] body) {
        try {
            Codec<?> codec = ProtobufProxy.create(clazz);
            return codec.decode(body);
        } catch (Exception e) {
            logger.error("protobuf codec decode failed for message {}", clazz.getName(), e);
            throw new MessageCodecException("protobuf codec decode failed for message " + clazz.getName(), e);
        }
    }

    @Override
    public byte[] encode(Object message) {
        // Write the content of the specific message
        byte[] body = null;
        Class msgClazz = message.getClass();
        try {
            Codec<Object> codec = ProtobufProxy.create(msgClazz);
            body = codec.encode(message);
        } catch (Exception e) {
            logger.error("protobuf codec encode failed for message {}", message.getClass().getName(), e);
            throw new MessageCodecException("protobuf codec encode failed for message " + message.getClass().getName(), e);
        }
        return body;
    }

}
