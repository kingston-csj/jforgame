package com.kingston.jforgame.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class ByteSerializer extends Serializer {

	@Override
	public Byte decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readByte(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeByte(out, (byte)value);
	}

}
