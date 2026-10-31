package jforgame.codec.struct;

import java.nio.ByteBuffer;

/**
 * Codec for signed 64-bit integers, wire-compatible with Protobuf {@code sint64}.
 *
 * <p>Values are encoded using ZigZag mapping followed by Base128 VarInt,
 * so small-magnitude negatives are stored compactly (1~10 bytes) rather than
 * always 10 bytes like raw two's-complement VarInt.</p>
 *
 * <p>Range: {@code -2^63} to {@code 2^63 - 1}, identical to Java {@code long}.</p>
 */
public class VarLongCodec extends Codec {

    /**
     * Decode ByteBuffer to Long.
     * Read VarInt raw value first, then apply ZigZag decode to restore signed long.
     */
    @Override
    public Long decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
        long rawUnsigned = readVarInt64(in);
        return zigZagDecode64(rawUnsigned);
    }

    @Override
    public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
        long num = (Long) value;
        long zigZagVal = zigZagEncode64(num);
        writeVarInt64(out, zigZagVal);
    }

    private long zigZagEncode64(long n) {
        return (n << 1) ^ (n >> 63);
    }

    private long zigZagDecode64(long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    private void writeVarInt64(ByteBuffer out, long value) {
        long v = value;
        while (true) {
            if ((v & ~0x7FL) == 0) {
                out.put((byte) v);
                return;
            }
            out.put((byte) ((v & 0x7F) | 0x80));
            v >>>= 7;
        }
    }

    private long readVarInt64(ByteBuffer in) {
        long result = 0;
        for (int i = 0; i < 10; i++) {
            int b = in.get() & 0xFF;
            // Only one payload bit remains in the tenth byte; no continuation.
            if (i == 9 && (b & 0xFE) != 0) {
                throw new IllegalArgumentException("VarInt overflow, exceeds sint64 max 10 bytes.");
            }
            result |= (b & 0x7FL) << (i * 7);
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new IllegalArgumentException("VarInt overflow, exceeds sint64 max 10 bytes.");
    }
}
