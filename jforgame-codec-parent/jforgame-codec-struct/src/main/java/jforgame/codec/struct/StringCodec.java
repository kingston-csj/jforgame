package jforgame.codec.struct;

import java.nio.ByteBuffer;

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
