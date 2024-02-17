package jforgame.socket.codec.struct;

import jforgame.socket.codec.MessageDecoder;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class StructMessageDecoder implements MessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(StructMessageDecoder.class);

	public Object readMessage(Class<?> msgClazz, byte[] body) {
		// 消息序列化这里的buff已经是一个完整的包体
		ByteBuffer in = ByteBuffer.allocate(body.length);
		in.put(body);
		in.flip();
		
		try {
			Codec messageCodec = Codec.getSerializer(msgClazz);
			Object message =  messageCodec.decode(in, msgClazz, null);
			return message;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

}
