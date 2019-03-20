package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class FloatSerializer extends Serializer {

	@Override
	public Float decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readFloat(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeFloat(out, (float)value);
	}

}
