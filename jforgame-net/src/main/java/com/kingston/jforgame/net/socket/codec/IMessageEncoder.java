package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.net.socket.message.Message;

public interface IMessageEncoder extends ProtocolEncoder {

	/**
	 * 把一个具体的消息序列化byte[]
	 * @param message
	 * @return
	 */
	byte[] writeMessageBody(Message message);

}
