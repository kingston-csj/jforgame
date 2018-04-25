package com.kingston.jforgame.net.socket.session;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

public enum SessionManager {

	INSTANCE;

	/** distributeKey auto generator  */
	private AtomicInteger distributeKeyGenerator = new AtomicInteger();
	/** key=playerId, value=session */
	private ConcurrentMap<Long, IoSession> player2sessions = new ConcurrentHashMap<>();


	public void registerNewPlayer(long playerId, IoSession session) {
		//biding playeId to session
		session.setAttribute(SessionProperties.PLAYER_ID, playerId);
		this.player2sessions.put(playerId, session);
	}

	/**
	 * get session's playerId
	 * @param session
	 * @return
	 */
	public long getPlayerIdBy(IoSession session) {
		if (session != null) {
			if (session.containsAttribute(SessionProperties.PLAYER_ID)) {
				return getSessionAttr(session, SessionProperties.PLAYER_ID, Long.class);
			}
		}
		return 0;
	}

	public IoSession getSessionBy(long playerId) {
		return player2sessions.get(playerId);
	}

	/**
	 * get appointed sessionAttr
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttr(IoSession session, AttributeKey attrKey, Class<T> attrType) {
		return (T)session.getAttribute(attrKey);
	}

	public int getNextDistributeKey() {
		return this.distributeKeyGenerator.getAndIncrement();
	}

	public String getRemoteIp(IoSession session) {
		return ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
	}

}
