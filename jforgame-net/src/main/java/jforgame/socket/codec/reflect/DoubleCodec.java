package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.utils.ByteBuffUtil;

public class DoubleCodec extends Codec {

	@Override
	public Double decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readDouble(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeDouble(out, (double)value);
	}

}
