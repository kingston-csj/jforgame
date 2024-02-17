package jforgame.codec.struct;

import java.nio.ByteBuffer;

public class ByteCodec extends Codec {

	@Override
	public Byte decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readByte(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeByte(out, (byte)value);
	}

}
