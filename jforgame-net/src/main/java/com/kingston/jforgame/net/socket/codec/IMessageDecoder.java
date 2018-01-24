package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.filter.codec.ProtocolDecoder;

import com.kingston.jforgame.net.socket.message.Message;

public interface IMessageDecoder extends ProtocolDecoder {
	
	/**
	 * 根据消息元信息反序列号为消息
	 * @param module
	 * @param cmd
	 * @param body
	 * @return
	 */
	Message readMessage(short module, short cmd, byte[] body);
	
}
