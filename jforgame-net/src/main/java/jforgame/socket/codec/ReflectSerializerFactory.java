package jforgame.socket.codec;

import jforgame.socket.codec.reflect.ReflectDecoder;
import jforgame.socket.codec.reflect.ReflectEncoder;

public class ReflectSerializerFactory implements SerializerFactory {
	
	private PrivateProtocolDecoder decoder = new ReflectDecoder();
	
	private PrivateProtocolEncoder encoder = new ReflectEncoder();
	
	@Override
	public PrivateProtocolDecoder getDecoder() {
		return decoder;
	}

	@Override
	public PrivateProtocolEncoder getEncoder() {
		return encoder;
	}

}
