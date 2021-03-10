package jforgame.socket.mina;

import java.util.HashMap;
import java.util.Map;

import jforgame.socket.message.Message;
import org.apache.mina.core.session.IoSession;

import jforgame.socket.IdSession;

public class MinaSession implements IdSession {
	
	private IoSession session;
	
	/** 拓展用，保存一些个人数据  */
	private Map<String, Object> attrs = new HashMap<>();
	
	public MinaSession(IoSession session) {
		this.session = session;
	}

	@Override
	public void sendPacket(Message packet) {
		session.write(packet);
	}

	@Override
	public long getOwnerId() {
		if (attrs.containsKey(ID)) {
			return (long) attrs.get(ID);
		}
		return 0;
	}
	
	@Override
	public Object getAttribute(String key) {
		return attrs.get(key);
	}
	
	@Override
	public Object setAttribute(String key, Object value) {
		attrs.put(key, value);
		return value;
	}
}
