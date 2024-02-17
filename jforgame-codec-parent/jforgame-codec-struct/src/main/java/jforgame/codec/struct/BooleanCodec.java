package jforgame.codec.struct;

import java.nio.ByteBuffer;

public class BooleanCodec extends Codec {

	@Override
	public Boolean decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		return ByteBuffUtil.readBoolean(in);
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		ByteBuffUtil.writeBoolean(out, (boolean)value);
	}

}
