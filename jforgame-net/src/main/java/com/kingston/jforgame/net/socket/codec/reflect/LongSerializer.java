package com.kingston.jforgame.net.socket.codec.reflect;

import org.apache.mina.core.buffer.IoBuffer;

public class LongSerializer extends Serializer {

	@Override
	public Long decode(IoBuffer in, Class type, Class wrapper) {
		return Long.valueOf(in.getLong());
	}
	

	@Override
	public void encode(IoBuffer out, Object value, Class wrapper) {
		out.putLong((long)value);
	}
	

}
