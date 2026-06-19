package jforgame.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Protobuf message encoder and decoder.
 * Using this codec, server and client communicate through protobuf.
 * For a message class, the server only needs to add {@link ProtobufClass} annotation on the message class.
 * The server can generate the corresponding .proto file through {@link ProtobufIDLGenerator}.
 */
public class ProtobufCodec implements MessageCodec {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufCodec.class);

	@Override
	public Object decode(Class<?> msgClazz, byte[] body) {
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
            return codec.decode(body);
		} catch (IOException e) {
			logger.error("read message {} failed", msgClazz.getName(),e);
		}
		return null;
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
			logger.error("read message failed", e);
		}
		return body;
	}

}
