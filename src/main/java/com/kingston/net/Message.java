package com.kingston.net;

import com.kingston.net.annotation.Protocol;

public abstract class Message {
	
	public short getModule() {
		Protocol annotation = getClass().getAnnotation(Protocol.class);
		if (annotation != null) {
			return annotation.module();
		}
		return 0;
	}
	
	public short getCmd() {
		Protocol annotation = getClass().getAnnotation(Protocol.class);
		if (annotation != null) {
			return annotation.cmd();
		}
		return 0;
	}
	
	public String key() {
		return this.getModule() + "_" + this.getCmd();
	}

}
