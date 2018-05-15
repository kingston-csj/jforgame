package com.kingston.jforgame.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class FloatSerializer extends Serializer {

	@Override
	public Float decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readFloat(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeFloat(out, (float)value);
	}

}
