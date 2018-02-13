package com.kingston.jforgame.net.socket.message;

import com.kingston.jforgame.net.socket.annotation.MessageMeta;

/**
 * base class of IO message
 */
public abstract class Message {

	/**
	 * messageMeta, module of message
	 * @return
	 */
	public short getModule() {
		MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
		if (annotation != null) {
			return annotation.module();
		}
		return 0;
	}

	/**
	 * messageMeta, subType of module
	 * @return
	 */
	public short getCmd() {
		MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
		if (annotation != null) {
			return annotation.cmd();
		}
		return 0;
	}

	public String key() {
		return this.getModule() + "_" + this.getCmd();
	}

}
