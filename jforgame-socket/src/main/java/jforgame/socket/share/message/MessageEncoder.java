package jforgame.socket.share.message;

/**
 * 私有协议栈消息编码器
 * @author kinson
 *
 */
public interface MessageEncoder {

	/**
	 * 把一个具体的消息序列化byte[]
	 * @param message
	 * @return
	 */
	byte[] writeMessageBody(Object message);

}
