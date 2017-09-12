package com.kingston.net;

import com.kingston.net.annotation.MessageMeta;

/**
 * 通信消息体定义
 */
public abstract class Message {
	
	public short getModule() {
		MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
		if (annotation != null) {
			return annotation.module();
		}
		return 0;
	}
	
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
