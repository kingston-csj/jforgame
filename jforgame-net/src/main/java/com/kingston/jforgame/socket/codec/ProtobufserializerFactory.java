package com.kingston.jforgame.socket.codec;

import com.kingston.jforgame.socket.codec.protobuf.ProtobufDecoder;
import com.kingston.jforgame.socket.codec.protobuf.ProtobufEncoder;

public class ProtobufserializerFactory implements SerializerFactory {
	
	private IMessageDecoder decoder = new ProtobufDecoder();
	
	private IMessageEncoder encoder = new ProtobufEncoder();

	@Override
	public IMessageDecoder getDecoder() {
		return decoder;
	}

	@Override
	public IMessageEncoder getEncoder() {
		return encoder;
	}

}
