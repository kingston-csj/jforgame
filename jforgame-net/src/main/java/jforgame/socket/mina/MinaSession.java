package jforgame.socket.mina;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import jforgame.socket.message.Message;
import org.apache.commons.lang3.StringUtils;
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
	public InetSocketAddress getRemoteAddress() {
		if (session == null) {
			return null;
		}
		return ((InetSocketAddress) session.getRemoteAddress());
	}

	@Override
	public String getRemoteIP() {
		if (session == null) {
			return StringUtils.EMPTY;
		}
		return ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
	}

	@Override
	public int getRemotePort() {
		if (session == null) {
			return -1;
		}
		return ((InetSocketAddress) session.getRemoteAddress()).getPort();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		if (session == null) {
			return null;
		}
		return ((InetSocketAddress) session.getLocalAddress());
	}

	@Override
	public String getLocalIP() {
		if (session == null) {
			return StringUtils.EMPTY;
		}
		return ((InetSocketAddress) session.getLocalAddress()).getAddress().getHostAddress();
	}

	@Override
	public int getLocalPort() {
		if (session == null) {
			return -1;
		}
		return ((InetSocketAddress) session.getLocalAddress()).getPort();
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
