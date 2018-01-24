package com.kingston.jforgame.net.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

public class BooleanSerializer extends Serializer {

	@Override
	public Boolean decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return in.get() == 1;
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.put((boolean) value ? (byte)1 : (byte)0);
	}

}
