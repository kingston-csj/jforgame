package com.kingston.jforgame.net.socket.codec.protobuf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.kingston.jforgame.net.socket.codec.IMessageDecoder;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.net.socket.message.MessageFactory;

/**
 * @author kingston
 */
public class ProtobufDecoder implements IMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ProtobufDecoder.class);

	@Override
	public Message readMessage(short module, short cmd, byte[] body) {
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
