package com.kingston.jforgame.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class BooleanSerializer extends Serializer {

	@Override
	public Boolean decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readBoolean(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeBoolean(out, (boolean)value);
	}

}
