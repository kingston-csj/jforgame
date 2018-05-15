package com.kingston.jforgame.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class ShortSerializer extends Serializer {

	@Override
	public Short decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readShort(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeShort(out, (short)value);
	}

}
