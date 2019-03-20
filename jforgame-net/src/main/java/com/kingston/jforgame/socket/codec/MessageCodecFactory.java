package com.kingston.jforgame.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.kingston.jforgame.socket.codec.mina.MinaProtocolDecoder;
import com.kingston.jforgame.socket.codec.mina.MinaProtocolEncoder;

/**
 * @author kingston
 */
public class MessageCodecFactory implements ProtocolCodecFactory {

	private MinaProtocolDecoder decoder = new MinaProtocolDecoder();

	private MinaProtocolEncoder encoder = new MinaProtocolEncoder();

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}
