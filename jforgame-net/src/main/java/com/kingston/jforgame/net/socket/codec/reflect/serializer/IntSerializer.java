package com.kingston.jforgame.net.socket.codec.reflect.serializer;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.net.socket.utils.ByteBuffUtil;

public class IntSerializer extends Serializer {

	@Override
	public Integer decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readInt(in);
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeInt(out, (int)value);
	}

}
