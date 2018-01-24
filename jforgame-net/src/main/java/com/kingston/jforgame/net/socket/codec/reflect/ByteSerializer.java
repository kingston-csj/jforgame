package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class ByteSerializer extends Serializer {

	@Override
	public Byte decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return in.get();
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.put((byte)value);
	}

}
