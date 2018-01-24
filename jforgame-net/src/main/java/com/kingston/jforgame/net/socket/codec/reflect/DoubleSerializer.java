package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class DoubleSerializer extends Serializer {

	@Override
	public Double decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return in.getDouble();
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.putDouble((double)value);
	}

}
