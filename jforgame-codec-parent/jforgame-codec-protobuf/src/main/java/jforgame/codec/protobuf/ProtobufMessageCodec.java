package jforgame.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author kinson
 */
public class ProtobufMessageCodec implements MessageCodec {

	private static Logger logger = LoggerFactory.getLogger(ProtobufMessageCodec.class);

	@Override
	public Object decode(Class<?> msgClazz, byte[] body) {
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Object message = codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("read message {} failed, exception {}", new Object[]{msgClazz.getName() ,e});
		}
		return null;
	}

	@Override
	public byte[] encode(Object message) {
		//写入具体消息的内容
		byte[] body = null;
		Class msgClazz = message.getClass();
		try {
			Codec<Object> codec = ProtobufProxy.create(msgClazz);
			body = codec.encode(message);
		} catch (Exception e) {
			logger.error("read message {} failed , exception {}",
					new Object[]{message.getClass(), e});
		}
		return body;
	}

}
