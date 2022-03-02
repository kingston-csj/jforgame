package jforgame.socket.codec;

import jforgame.socket.message.MessageDecoder;
import jforgame.socket.message.MessageEncoder;

/**
 * @author kinson
 */
public class SerializerHelper {

	public static volatile SerializerHelper instance;

	/**
	 * 消息私有协议栈编解码
	 */
	private MinaMessageCodecFactory codecFactory;

	/**
	 * 消息序列化编解码
	 */
	private static MessageCodecFactory serializerFactory = new StructPrivateProtocolCodec();

	public static SerializerHelper getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (SerializerHelper.class) {
			if (instance == null) {
				SerializerHelper self =  new SerializerHelper();
				self.initialize();
				instance = self;
			}
		}
		return instance;
	}

	private void initialize() {
		codecFactory = new MinaMessageCodecFactory();
	}

	public MinaMessageCodecFactory getCodecFactory() {
		return codecFactory;
	}

	public MessageDecoder getDecoder() {
		return serializerFactory.getDecoder();
	}

	public MessageEncoder getEncoder() {
		return serializerFactory.getEncoder();
	}

	public MessageCodecFactory getSerializerFactory() {
		return serializerFactory;
	}

}
