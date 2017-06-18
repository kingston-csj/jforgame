package com.kingston.net.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MessageCodecFactory implements ProtocolCodecFactory{
	
	private static MessageCodecFactory instance = new MessageCodecFactory();
	
	private MessageDecoder decoder;
	
	private MessageEncoder encoder;
	
	private MessageCodecFactory() {
		decoder = new MessageDecoder();
		encoder = new MessageEncoder();
	}
	
	public static MessageCodecFactory getInstance() {
		return instance;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}

}
