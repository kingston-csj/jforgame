package com.kingston.jforgame.net.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.net.socket.utils.ByteBuffUtil;

public class LongSerializer extends Serializer {

	@Override
	public Long decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readLong(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeLong(out, (long)value);
	}

}
