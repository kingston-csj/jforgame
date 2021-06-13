package jforgame.socket.codec;

/**
 * @author kinson
 */
public class SerializerHelper {

	public static volatile SerializerHelper instance;

	/**
	 * 消息私有协议栈编解码
	 */
	private MessageCodecFactory codecFactory;

	/**
	 * 消息序列化编解码
	 */
	private static SerializerFactory serializerFactory = new ReflectSerializerFactory();

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
		codecFactory = new MessageCodecFactory();
	}

	public MessageCodecFactory getCodecFactory() {
		return codecFactory;
	}

	public IMessageDecoder getDecoder() {
		return serializerFactory.getDecoder();
	}

	public IMessageEncoder getEncoder() {
		return serializerFactory.getEncoder();
	}

	public SerializerFactory getSerializerFactory() {
		return serializerFactory;
	}

}
