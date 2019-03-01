package com.kingston.jforgame.socket.codec;

import com.kingston.jforgame.socket.codec.reflect.ReflectDecoder;
import com.kingston.jforgame.socket.codec.reflect.ReflectEncoder;

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
