package com.kingston.jforgame.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;

public class StringSerializer extends Serializer {

	@Override
	public String decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readUtf8(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeUtf8(out, (String)value);
	}

}
