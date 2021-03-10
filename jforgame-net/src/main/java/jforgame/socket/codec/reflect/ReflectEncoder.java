package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.mina.CodecProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.socket.codec.IMessageEncoder;
import jforgame.socket.message.Message;

public class ReflectEncoder implements IMessageEncoder {

	private static Logger logger = LoggerFactory.getLogger(ReflectEncoder.class);

	@Override
	public byte[] writeMessageBody(Message message) {
		ByteBuffer out = ByteBuffer.allocate(CodecProperties.WRITE_CAPACITY);
		//写入具体消息的内容
		try {
			Codec messageCodec = Codec.getSerializer(message.getClass());
			messageCodec.encode(out, message, null);
		} catch (Exception e) {
			logger.error("读取消息出错,模块号{}，类型{},异常{}",
					new Object[]{message.getModule(), message.getCmd() ,e});
		}
		out.flip();

		byte[] body = new byte[out.remaining()];
		out.get(body);
		return body;
	}

}
