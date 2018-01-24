package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.net.socket.codec.protobuf.ProtobufDecoder;
import com.kingston.jforgame.net.socket.codec.protobuf.ProtobufEncoder;

public class ProtobufCodecFactory implements ProtocolCodecFactory {

	private static ProtobufCodecFactory instance = new ProtobufCodecFactory();

	private ProtocolDecoder decoder;

	private ProtocolEncoder encoder;

	private ProtobufCodecFactory() {
		decoder = new ProtobufDecoder();
		encoder = new ProtobufEncoder();
	}

	public static ProtocolCodecFactory getInstance() {
		return instance;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}

}
