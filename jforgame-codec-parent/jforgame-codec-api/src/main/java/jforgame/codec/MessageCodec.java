package jforgame.codec;

/**
 * 私有协议栈消息解码器
 * 这里只是对消息体本身进行编码解码，不包含消息头
 * 常见的消息编码解码方式有：
 * 1. json
 * 2. protobuf
 * 3. messagepack
 * 4. struct(自定义，基于bean结构体)
 */
public interface MessageCodec {

	/**
	 * 	根据消息元信息反序列号为消息
	 *  body已经是一个完整的消息包体，所以解码buff不需要复杂的操作，用NIO的ByteBuff即可
	 *
	 * @param clazz class of the message
	 * @param body  data body of the message
	 * @return request message
	 */
	Object decode(Class<?> clazz, byte[] body);

	/**
	 * 把一个具体的消息序列化byte[]
	 * @param message message to encode
	 * @return byte array of the message
	 */
	byte[] encode(Object message);

}
