package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class DoubleSerializer extends Serializer {

	@Override
	public Double decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readDouble(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeDouble(out, (double)value);
	}

}
