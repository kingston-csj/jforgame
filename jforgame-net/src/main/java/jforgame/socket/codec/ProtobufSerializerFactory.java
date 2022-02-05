package jforgame.socket.codec;

import jforgame.socket.codec.protobuf.ProtobufDecoder;
import jforgame.socket.codec.protobuf.ProtobufEncoder;

public class ProtobufSerializerFactory implements SerializerFactory {
	
	private PrivateProtocolDecoder decoder = new ProtobufDecoder();
	
	private PrivateProtocolEncoder encoder = new ProtobufEncoder();

	@Override
	public PrivateProtocolDecoder getDecoder() {
		return decoder;
	}

	@Override
	public PrivateProtocolEncoder getEncoder() {
		return encoder;
	}

}
