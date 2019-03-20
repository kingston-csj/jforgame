package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class LongSerializer extends Serializer {

	@Override
	public Long decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readLong(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeLong(out, (long)value);
	}

}
