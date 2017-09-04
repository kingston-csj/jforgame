package com.kingston.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

public enum SessionManager {
	
	INSTANCE;
	
	/** 分发器索引生成器 */
	private AtomicInteger distributeKeyGenerator = new AtomicInteger();
	/** playerid与session的对应关系 */
	private ConcurrentMap<Long, IoSession> player_sessions = new ConcurrentHashMap<>();
	
	
	public void registerNewPlayer(long playerId, IoSession session) {
		//绑定session与玩家id
		session.setAttribute(SessionProperties.PLAYER_ID, playerId);
		this.player_sessions.put(playerId, session);
	}
	
	/**
	 * 获取session对应的角色id
	 * @param session
	 * @return
	 */
	public long getPlayerIdBy(IoSession session) {
		long result = 0;
		if (session != null) {
			result = getSessionAttr(session, SessionProperties.PLAYER_ID, Long.class);
		}
		return result;
	}
	
	public IoSession getSessionBy(long playerId) {
		return player_sessions.get(playerId);
	}
	
	/**
	 * 获取session指定属性类型的值
	 * @param session
	 * @param attrKey
	 * @param attrType
	 * @return
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
