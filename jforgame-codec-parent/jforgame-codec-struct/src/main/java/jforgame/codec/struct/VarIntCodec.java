package jforgame.codec.struct;

import java.nio.ByteBuffer;

/**
 * Codec for signed 32-bit integers, wire-compatible with Protobuf {@code sint32}.
 *
 * <p>Values are encoded using ZigZag mapping followed by Base128 VarInt,
 * so small-magnitude negatives (e.g. -1, -2) occupy the same number of bytes
 * as small positives, instead of always taking 10 bytes as raw two's-complement
 * VarInt would.</p>
 *
 * <p>Range: {@code -2^31} to {@code 2^31 - 1}, identical to Java {@code int}.</p>
 */
public class VarIntCodec extends Codec {

    /**
     * Decode ByteBuffer to Integer.
     * Read VarInt raw value first, then apply ZigZag decode to restore signed int.
     *
     * @param in      input byte buffer
     * @param type    target value type
     * @param wrapper wrapper class type
     * @return decoded signed int value
     */
    @Override
    public Integer decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
        int rawUnsigned = readVarInt(in);
        return zigZagDecode32(rawUnsigned);
    }

    /**
     * Encode Integer to ByteBuffer.
     * Convert signed int to zigzag unsigned value, then write as Base128 VarInt.
     *
     * @param out     output byte buffer
     * @param value   source integer value
     * @param wrapper wrapper class type
     */
    @Override
    public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
        int num = (Integer) value;
        int zigZagVal = zigZagEncode32(num);
        writeVarInt(out, zigZagVal);
    }

    /**
     * ZigZag encode for int32, same as protobuf sint32.
     * Map signed int32 to uint32, compress small negative number.
     * Formula: (n << 1) ^ (n >> 31)
     *
     * @param n signed 32-bit integer
     * @return zigzag encoded unsigned int
     */
    private int zigZagEncode32(int n) {
        return (n << 1) ^ (n >> 31);
    }

    /**
     * ZigZag decode for int32, same as protobuf sint32.
     * Restore signed int32 from zigzag mapped unsigned value.
     * Formula: (n >>> 1) ^ -(n & 1)
     *
     * @param n zigzag encoded unsigned int
     * @return original signed int32
     */
    private int zigZagDecode32(int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    /**
     * Write Base128 VarInt into ByteBuffer.
     * Same varint encoding as Protobuf wire type 0.
     * Max 5 bytes for 32-bit value.
     *
     * @param out   output buffer
     * @param value unsigned 32-bit integer after zigzag encode
     */
    private void writeVarInt(ByteBuffer out, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.put((byte) value);
                return;
            }
            out.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
    }

    /**
     * Read Base128 VarInt from ByteBuffer.
     * Same varint decoding as Protobuf wire type 0.
     * Reject values exceeding 32 bits or 5 bytes.
     *
     * @param in input buffer
     * @return raw unsigned varint value
     */
    private int readVarInt(ByteBuffer in) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            int b = in.get() & 0xFF;
            // Only four payload bits remain in the fifth byte; no continuation.
            if (i == 4 && (b & 0xF0) != 0) {
                throw new IllegalArgumentException("VarInt overflow, exceeds sint32 max 5 bytes.");
            }
            result |= (b & 0x7F) << (i * 7);
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new IllegalArgumentException("VarInt overflow, exceeds sint32 max 5 bytes.");
    }
}
