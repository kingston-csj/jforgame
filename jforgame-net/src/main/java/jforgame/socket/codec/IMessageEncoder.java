package jforgame.socket.codec;

import jforgame.socket.message.Message;

/**
 * 私有协议栈消息编码器
 * @author kinson
 *
 */
public interface IMessageEncoder {

	/**
	 * 把一个具体的消息序列化byte[]
	 * @param message
	 * @return
	 */
	byte[] writeMessageBody(Message message);

}
