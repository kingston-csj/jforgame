package jforgame.server.cross.core.server;

import org.apache.mina.core.session.IoSession;

import jforgame.socket.message.Message;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * session包装类，便于后续拓展，以及增强标识度
 *
 */
public class SCSession {
	
	private IoSession wrapper;
	
	private String clientIp;
	
	private int id;
	
	private static AtomicInteger idFactory = new AtomicInteger();
	
	public static SCSession valueOf(IoSession wrapper) {
		SCSession cSession = new SCSession();
		cSession.wrapper = wrapper;
		cSession.clientIp = wrapper.getRemoteAddress().toString();
		cSession.id = idFactory.incrementAndGet();
		return cSession;
	}
	
	
	
	public int getId() {
		return id;
	}

	public IoSession getWrapper() {
		return wrapper;
	}
	
	public String getClientIp() {
		return clientIp;
	}
	
	public void sendMessage(Message message) {
		this.wrapper.write(message);
	}
	
}
