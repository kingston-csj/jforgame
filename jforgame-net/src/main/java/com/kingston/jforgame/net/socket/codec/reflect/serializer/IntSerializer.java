package com.kingston.jforgame.net.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

public class IntSerializer extends Serializer {

	@Override
	public Integer decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return Integer.valueOf(in.getInt());
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.putInt((int)value);
	}

}
