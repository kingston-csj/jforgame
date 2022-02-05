package jforgame.socket.codec;

/**
 * 消息序列化工厂
 *
 */
public interface SerializerFactory {
	
	/**
	 * 生成解码器
	 * @return
	 */
	PrivateProtocolDecoder getDecoder();
	
	/**
	 * 生成编码器
	 * @return
	 */
	PrivateProtocolEncoder getEncoder();

}
