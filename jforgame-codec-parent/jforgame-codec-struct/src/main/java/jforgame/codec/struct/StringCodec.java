package jforgame.codec.struct;

import java.nio.ByteBuffer;

/**
 * String encoding and decoding (uses utf8 encoding)
 */
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
