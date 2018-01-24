package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class StringSerializer extends Serializer {

	@Override
	public String decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtils.readUtf8(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtils.writeUtf8(out, (String)value);
	}

}
