package com.kingston.jforgame.net.socket.codec;

public class SerializerHelper {
	
	private MessageCodecFactory codecFactory = new ProtobufCodecFactory();
	
//	private MessageCodecFactory codecFactory = new ReflectCodecFactory();
	
	public static SerializerHelper instance = new SerializerHelper();
	
	public static SerializerHelper getInstance() {
		return instance;
	}
	
	public MessageCodecFactory getCodecFactory() {
		return codecFactory;
	}
	
}
