package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.message.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.socket.codec.PrivateProtocolDecoder;
import jforgame.socket.message.Message;

public class ReflectDecoder implements PrivateProtocolDecoder {

	private static Logger logger = LoggerFactory.getLogger(ReflectDecoder.class);

	@Override
	public Message readMessage(int cmd, byte[] body) {
		// 消息序列化这里的buff已经是一个完整的包体
		ByteBuffer in = ByteBuffer.allocate(body.length);
		in.put(body);
		in.flip();
		
		Class<?> msgClazz = MessageFactoryImpl.getInstance().getMessage(cmd);
		try {
			Codec messageCodec = Codec.getSerializer(msgClazz);
			Message message = (Message) messageCodec.decode(in, msgClazz, null);
			return message;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

}
