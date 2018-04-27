package com.kingston.jforgame.net.socket.codec;

import com.kingston.jforgame.net.socket.codec.reflect.ReflectDecoder;
import com.kingston.jforgame.net.socket.codec.reflect.ReflectEncoder;

/**
 * @author kingston
 */
public class SerializerHelper {

	public static volatile SerializerHelper instance;

	private MessageCodecFactory codecFactory;

	private IMessageDecoder decoder;

	private IMessageEncoder encoder;

	public static SerializerHelper getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (SerializerHelper.class) {
			if (instance == null) {
				instance =  new SerializerHelper();
				instance.initialize();
			}
		}
		return instance;
	}

	private void initialize() {
//		decoder = new ProtobufDecoder();
//		encoder = new ProtobufEncoder();
		decoder = new ReflectDecoder();
		encoder = new ReflectEncoder();
		codecFactory = new MessageCodecFactory();
	}

	public MessageCodecFactory getCodecFactory() {
		return codecFactory;
	}

	public IMessageDecoder getDecoder() {
		return decoder;
	}

	public IMessageEncoder getEncoder() {
		return encoder;
	}

}
