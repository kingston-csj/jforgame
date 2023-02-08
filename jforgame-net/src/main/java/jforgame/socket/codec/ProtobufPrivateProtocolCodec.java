package jforgame.socket.codec;

import jforgame.socket.codec.protobuf.ProtobufMessageDecoder;
import jforgame.socket.codec.protobuf.ProtobufMessageEncoder;
import jforgame.socket.share.message.MessageDecoder;
import jforgame.socket.share.message.MessageEncoder;
import jforgame.socket.share.message.MessageFactory;

public class ProtobufPrivateProtocolCodec implements MessageCodecFactory {

	private MessageFactory messageFactory;

	private MessageDecoder decoder = new ProtobufMessageDecoder(messageFactory);
	
	private MessageEncoder encoder = new ProtobufMessageEncoder();

	public ProtobufPrivateProtocolCodec(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	public MessageDecoder getDecoder() {
		return decoder;
	}

	@Override
	public MessageEncoder getEncoder() {
		return encoder;
	}

}
