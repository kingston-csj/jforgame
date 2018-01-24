package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.net.socket.codec.protobuf.ProtobufDecoder;
import com.kingston.jforgame.net.socket.codec.protobuf.ProtobufEncoder;

public class ProtobufCodecFactory implements MessageCodecFactory {

	private IMessageDecoder decoder;

	private IMessageEncoder encoder;

	public ProtobufCodecFactory() {
		decoder = new ProtobufDecoder();
		encoder = new ProtobufEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}

	@Override
	public IMessageDecoder getMessageDecoder() {
		return decoder;
	}

	@Override
	public IMessageEncoder getMessageEncoder() {
		return encoder;
	}

}
