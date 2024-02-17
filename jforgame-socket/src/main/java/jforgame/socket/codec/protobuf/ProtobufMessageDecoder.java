package jforgame.socket.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import jforgame.socket.codec.MessageDecoder;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author kinson
 */
public class ProtobufMessageDecoder implements MessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufMessageDecoder.class);

	@Override
	public Object readMessage(Class<?> msgClazz, byte[] body) {
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
			Object message =  codec.decode(body);
			return message;
		} catch (IOException e) {
			logger.error("read message {} failed, exception {}", new Object[]{msgClazz.getName() ,e});
		}
		return null;
	}

}
