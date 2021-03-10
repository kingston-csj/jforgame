package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.socket.codec.IMessageDecoder;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageFactory;

public class ReflectDecoder implements IMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ReflectDecoder.class);

	@Override
	public Message readMessage(short module, byte cmd, byte[] body) {
		// 消息序列化这里的buff已经是一个完整的包体
		ByteBuffer in = ByteBuffer.allocate(body.length);
		in.put(body);
		in.flip();
		
		Class<?> msgClazz = MessageFactory.INSTANCE.getMessage(module, cmd);
		try {
			Codec messageCodec = Codec.getSerializer(msgClazz);
			Message message = (Message) messageCodec.decode(in, msgClazz, null);
			return message;
		} catch (Exception e) {
			logger.error("读取消息出错,模块号{}，类型{},异常{}", new Object[]{module, cmd ,e});
		}
		return null;
	}

}
