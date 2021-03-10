package jforgame.socket.codec.reflect;

import java.nio.ByteBuffer;

import jforgame.socket.utils.ByteBuffUtil;

public class StringCodec extends Codec {

	@Override
	public String decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readUtf8(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeUtf8(out, (String)value);
	}

}
