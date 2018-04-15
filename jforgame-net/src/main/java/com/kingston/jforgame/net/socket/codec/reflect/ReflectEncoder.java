package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.net.socket.codec.CodecContext;
import com.kingston.jforgame.net.socket.codec.IMessageEncoder;
import com.kingston.jforgame.net.socket.codec.reflect.serializer.Serializer;
import com.kingston.jforgame.net.socket.message.Message;

public class ReflectEncoder implements IMessageEncoder {

	private static Logger logger = LoggerFactory.getLogger(ReflectEncoder.class);

	@Override
	public byte[] writeMessageBody(Message message) {
		IoBuffer out = IoBuffer.allocate(CodecContext.WRITE_CAPACITY);
		//写入具体消息的内容
		try {
			Serializer messageCodec = Serializer.getSerializer(message.getClass());
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
