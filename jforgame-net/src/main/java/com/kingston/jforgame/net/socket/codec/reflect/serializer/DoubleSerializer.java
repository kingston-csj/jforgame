package com.kingston.jforgame.net.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.net.socket.utils.ByteBuffUtil;

public class DoubleSerializer extends Serializer {

	@Override
	public Double decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readDouble(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeDouble(out, (double)value);
	}

}
