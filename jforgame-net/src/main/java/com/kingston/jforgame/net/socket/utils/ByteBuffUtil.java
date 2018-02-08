package com.kingston.jforgame.net.socket.utils;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ByteBuffUtil {

	private static final Logger logger = LoggerFactory.getLogger(ByteBuffUtil.class);

	public static boolean readBoolean(IoBuffer buf) {
		return buf.get() > 0;
	}

	public static void writeBoolean(IoBuffer buf, boolean value) {
		buf.put(value ? (byte)1: (byte)0);
	}

	public static byte readByte(IoBuffer buf) {
		return buf.get();
	}

	public static void writeByte(IoBuffer buf, byte value) {
		buf.put(value);
	}

	public static char readChar(IoBuffer buf) {
		return buf.getChar();
	}

	public static void writeChar(IoBuffer buf, char value) {
		buf.putChar(value);
	}

	public static short readShort(IoBuffer buf) {
		return buf.getShort();
	}

	public static void writeShort(IoBuffer buf, short value) {
		buf.putShort(value);
	}

	public static int readInt(IoBuffer buf) {
		return buf.getInt();
	}

	public static void writeInt(IoBuffer buf, int value) {
		buf.putInt(value);
	}

	public static long readLong(IoBuffer buf) {
		return buf.getLong();
	}

	public static void writeLong(IoBuffer buf, long value) {
		buf.putLong(value);
	}

	public static float readFloat(IoBuffer buf) {
		return buf.getFloat();
	}

	public static void writeFloat(IoBuffer buf, float value) {
		buf.putFloat(value);
	}

	public static double readDouble(IoBuffer buf) {
		return buf.getDouble();
	}

	public static void writeDouble(IoBuffer buf, double value) {
		buf.putDouble(value);
	}


	public static String readUtf8(IoBuffer buf) {
		int strSize = buf.getInt();
		byte[] content = new byte[strSize];
		buf.get(content);
		try {
			return new String(content,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return "";
		}
	}

	public static void writeUtf8(IoBuffer buf, String msg) {
		byte[] content ;
		try {
			if (msg == null) {
				msg = "";
			}
			content = msg.getBytes("UTF-8");
			buf.putInt(content.length);
			buf.put(content);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
	}
}
