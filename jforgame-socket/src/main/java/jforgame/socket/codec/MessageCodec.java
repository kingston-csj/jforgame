package jforgame.socket.codec;

/**
 * 私有协议栈消息解码器
 */
public interface MessageCodec {

	/**
	 * 	根据消息元信息反序列号为消息
	 *  body已经是一个完整的消息包体，所以解码buff不需要复杂的操作，用NIO的ByteBuff即可
	 * 
	 * @param clazz class of the message
	 * @param body  data body of the message
	 * @return
	 */
	Object decode(Class<?> clazz, byte[] body);

	/**
	 * 把一个具体的消息序列化byte[]
	 * @param message
	 * @return
	 */
	byte[] encode(Object message);

}
