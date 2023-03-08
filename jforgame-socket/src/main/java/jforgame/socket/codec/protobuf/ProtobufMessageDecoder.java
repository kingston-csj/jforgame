package jforgame.socket.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import jforgame.socket.share.message.MessageDecoder;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author kinson
 */
public class ProtobufMessageDecoder implements MessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufMessageDecoder.class);

	private MessageFactory messageFactory;

	public ProtobufMessageDecoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	public Object readMessage(int cmd, byte[] body) {
		Class<?> msgClazz = messageFactory.getMessage(cmd);
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Object message =  codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("read message {} failed, exception {}", new Object[]{cmd ,e});
		}
		return null;
	}

}
