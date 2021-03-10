package jforgame.socket.codec.protobuf;

import java.io.IOException;

import jforgame.socket.codec.IMessageDecoder;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

/**
 * @author kinson
 */
public class ProtobufDecoder implements IMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufDecoder.class);

	@Override
	public Message readMessage(short module, byte cmd, byte[] body) {
		Class<?> msgClazz = MessageFactory.INSTANCE.getMessage(module, cmd);
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Message message = (Message) codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("读取消息出错,模块号{}，类型{},异常{}", new Object[]{module, cmd ,e});
		}
		return null;
	}

}
