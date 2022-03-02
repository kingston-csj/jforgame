package jforgame.socket.codec;

import jforgame.socket.codec.struct.ReflectDecoder;
import jforgame.socket.codec.struct.StructMessageEncoder;
import jforgame.socket.message.MessageDecoder;
import jforgame.socket.message.MessageEncoder;

public class StructPrivateProtocolCodec implements MessageCodecFactory {
	
	private MessageDecoder decoder = new ReflectDecoder();
	
	private MessageEncoder encoder = new StructMessageEncoder();
	
	@Override
	public MessageDecoder getDecoder() {
		return decoder;
	}

	@Override
	public MessageEncoder getEncoder() {
		return encoder;
	}

}
