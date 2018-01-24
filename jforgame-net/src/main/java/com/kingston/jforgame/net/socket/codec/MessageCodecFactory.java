package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

public interface MessageCodecFactory extends ProtocolCodecFactory {
	
	public IMessageDecoder getMessageDecoder();

	public IMessageEncoder getMessageEncoder();
	
}
