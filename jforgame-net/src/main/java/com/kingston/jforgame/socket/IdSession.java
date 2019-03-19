package com.kingston.jforgame.socket;

import com.kingston.jforgame.socket.message.Message;

public interface IdSession {

	static final String ID = "ID";
	
	void sendPacket(Message packet);
	
	long getOwnerId();
	
	public Object setAttribute(String key, Object value);
	
	Object getAttribute(String key);
	
}

