package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.utils.ByteBuffUtil;

public class IntCodec extends Codec {

	@Override
	public Integer decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readInt(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeInt(out, (int)value);
	}

}
