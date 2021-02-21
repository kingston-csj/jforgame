package com.kingston.jforgame.socket.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.jforgame.socket.codec.IMessageEncoder;
import com.kingston.jforgame.socket.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufEncoder implements IMessageEncoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufEncoder.class);

	@Override
	public byte[] writeMessageBody(Message message) {
		//写入具体消息的内容
		byte[] body = null;
		Class<Message> msgClazz = (Class<Message>) message.getClass();
		try {
			Codec<Message> codec = ProtobufProxy.create(msgClazz);
			body = codec.encode(message);
		} catch (Exception e) {
			logger.error("", e);
		}
		return body;
	}

}
