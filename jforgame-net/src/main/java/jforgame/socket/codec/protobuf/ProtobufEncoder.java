package jforgame.socket.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import jforgame.socket.codec.PrivateProtocolEncoder;
import jforgame.socket.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufEncoder implements PrivateProtocolEncoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufEncoder.class);

	@Override
	public byte[] writeMessageBody(Object message) {
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
