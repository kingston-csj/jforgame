package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.nio.ByteBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class BooleanSerializer extends Serializer {

	@Override
	public Boolean decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readBoolean(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeBoolean(out, (boolean)value);
	}

}
