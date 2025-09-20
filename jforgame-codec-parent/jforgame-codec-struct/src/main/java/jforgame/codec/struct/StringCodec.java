package jforgame.codec.struct;

import java.nio.ByteBuffer;

/**
 * 字符串编码解码（使用utf8编码）
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
