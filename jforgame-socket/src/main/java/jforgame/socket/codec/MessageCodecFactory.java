package jforgame.socket.codec;

import jforgame.socket.share.message.MessageDecoder;
import jforgame.socket.share.message.MessageEncoder;

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
