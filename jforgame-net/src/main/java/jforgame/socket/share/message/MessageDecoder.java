package jforgame.socket.share.message;

/**
 * 私有协议栈消息解码器
 * 
 * @author kinson
 *
 */
public interface MessageDecoder {

	/**
	 * 	根据消息元信息反序列号为消息
	 *  body已经是一个完整的消息包体，所以解码buff不需要复杂的操作，用NIO的ByteBuff即可
	 * 
	 * @param cmd
	 * @param body   完整的消息包体字节流
	 * @return
	 */
	Object readMessage(int cmd, byte[] body);

}
