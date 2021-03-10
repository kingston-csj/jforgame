package jforgame.socket.codec;

import jforgame.socket.codec.reflect.ReflectDecoder;
import jforgame.socket.codec.reflect.ReflectEncoder;

public class ReflectSerializerFactory implements SerializerFactory {
	
	private IMessageDecoder decoder = new ReflectDecoder();
	
	private IMessageEncoder encoder = new ReflectEncoder();
	
	@Override
	public IMessageDecoder getDecoder() {
		return decoder;
	}

	@Override
	public IMessageEncoder getEncoder() {
		return encoder;
	}

}
