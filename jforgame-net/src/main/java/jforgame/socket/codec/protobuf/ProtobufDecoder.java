package jforgame.socket.codec.protobuf;

import java.io.IOException;

import jforgame.socket.codec.PrivateProtocolDecoder;
import jforgame.socket.message.Message;
import jforgame.socket.message.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

/**
 * @author kinson
 */
public class ProtobufDecoder implements PrivateProtocolDecoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufDecoder.class);

	@Override
	public Message readMessage(int cmd, byte[] body) {
		Class<?> msgClazz = MessageFactoryImpl.getInstance().getMessage(cmd);
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Message message = (Message) codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("read message {} failed, exception {}", new Object[]{cmd ,e});
		}
		return null;
	}

}
