package jforgame.socket.codec;

import jforgame.socket.codec.protobuf.ProtobufMessageDecoder;
import jforgame.socket.codec.protobuf.ProtobufMessageEncoder;
import jforgame.socket.message.MessageDecoder;
import jforgame.socket.message.MessageEncoder;

public class ProtobufPrivateProtocolCodec implements MessageCodecFactory {
	
	private MessageDecoder decoder = new ProtobufMessageDecoder();
	
	private MessageEncoder encoder = new ProtobufMessageEncoder();

	@Override
	public MessageDecoder getDecoder() {
		return decoder;
	}

	@Override
	public MessageEncoder getEncoder() {
		return encoder;
	}

}
