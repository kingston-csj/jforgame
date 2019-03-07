package com.kingston.jforgame.server.cross.core.server;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.socket.message.Message;

/**
 * session包装类，便于后续拓展，以及增强标识度
 *
 */
public class SCSession {
	
	private IoSession wrapper;
	
	public static SCSession valueOf(IoSession wrapper) {
		SCSession cSession = new SCSession();
		cSession.wrapper = wrapper;
		return cSession;
	}
	
	public IoSession getWrapper() {
		return wrapper;
	}

	public void setWrapper(IoSession wrapper) {
		this.wrapper = wrapper;
	}
	
	public void sendMessage(Message message) {
		this.wrapper.write(message);
	}

}
