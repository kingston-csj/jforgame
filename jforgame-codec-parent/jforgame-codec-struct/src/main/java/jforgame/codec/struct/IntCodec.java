package jforgame.codec.struct;

import java.nio.ByteBuffer;

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
