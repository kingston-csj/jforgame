package com.kingston.jforgame.socket.codec;

import com.kingston.jforgame.socket.message.Message;

/**
 * 私有协议栈消息解码器
 * @author kingston
 *
 */
public interface IMessageDecoder {

	/**
	 * 根据消息元信息反序列号为消息
	 * @param module
	 * @param cmd
	 * @param body
	 * @return
	 */
	Message readMessage(short module, short cmd, byte[] body);

}
