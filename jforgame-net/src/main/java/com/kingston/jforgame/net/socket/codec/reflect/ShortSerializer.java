package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class ShortSerializer extends Serializer {

	@Override
	public Short decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return in.getShort();
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		out.putShort((short)value);
	}

}
