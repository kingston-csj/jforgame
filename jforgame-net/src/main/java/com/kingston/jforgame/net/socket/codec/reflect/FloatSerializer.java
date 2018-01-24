package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class FloatSerializer extends Serializer {

	@Override
	public Float decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return in.getFloat();
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.putFloat((float)value);
	}

}
