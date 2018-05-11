package com.kingston.jforgame.net.socket.codec;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author kingston
 */
public class CodecContext {

	public static final int READ_CAPACITY = 1024;

	public static final int WRITE_CAPACITY = 256;
	/** last interrupted　buff */
	private IoBuffer buffer;

	public CodecContext() {
		buffer = IoBuffer.allocate(READ_CAPACITY).setAutoExpand(true);
	}

	public IoBuffer append(IoBuffer in) {
		this.buffer.put(in);
		return this.buffer;
	}

	public IoBuffer getBuffer() {
		return this.buffer;
	}
}
