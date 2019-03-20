package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class IntSerializer extends Serializer {

	@Override
	public Integer decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readInt(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeInt(out, (int)value);
	}

}
