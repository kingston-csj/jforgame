package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.utils.ByteBuffUtil;

public class LongCodec extends Codec {

	@Override
	public Long decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readLong(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeLong(out, (long)value);
	}

}
