package jforgame.socket.codec;

import jforgame.socket.codec.protobuf.ProtobufDecoder;
import jforgame.socket.codec.protobuf.ProtobufEncoder;

public class ProtobufSerializerFactory implements SerializerFactory {
	
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
