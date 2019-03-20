package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class ByteSerializer extends Serializer {

	@Override
	public Byte decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readByte(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeByte(out, (byte)value);
	}

}
