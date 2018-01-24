package com.kingston.jforgame.net.socket.codec.reflect;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;

public final class ByteBuffUtils {
	
	public static String readUtf8(IoBuffer buf){
		int strSize = buf.getInt();
		byte[] content = new byte[strSize];
		buf.get(content);
		try {
			return new String(content,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void writeUtf8(IoBuffer buf,String msg){
		byte[] content ;
		try {
			if (msg == null) {
				msg = "";
			}
			content = msg.getBytes("UTF-8");
			buf.putInt(content.length);
			buf.put(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
