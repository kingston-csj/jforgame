package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.net.socket.codec.reflect.ReflectDecoder;
import com.kingston.jforgame.net.socket.codec.reflect.ReflectEncoder;

public class ReflectCodecFactory implements ProtocolCodecFactory {

	private static ReflectCodecFactory instance = new ReflectCodecFactory();

	private ProtocolDecoder decoder;

	private ProtocolEncoder encoder;

	private ReflectCodecFactory() {
		decoder = new ReflectDecoder();
		encoder = new ReflectEncoder();
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
