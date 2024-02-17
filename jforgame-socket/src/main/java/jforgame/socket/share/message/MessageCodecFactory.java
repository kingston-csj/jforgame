package jforgame.socket.share.message;

import jforgame.socket.codec.MessageDecoder;
import jforgame.socket.codec.MessageEncoder;

/**
 * 消息序列化工厂
 *
 */
public interface MessageCodecFactory {
	
	/**
	 * 生成解码器
	 * @return
	 */
	MessageDecoder getDecoder();
	
	/**
	 * 生成编码器
	 * @return
	 */
	MessageEncoder getEncoder();

}
