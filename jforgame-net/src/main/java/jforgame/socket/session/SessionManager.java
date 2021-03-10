package jforgame.socket.session;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jforgame.socket.IdSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

public enum SessionManager {

	/** 枚举单例 */
	INSTANCE;

	/** distributeKey auto generator  */
	private AtomicInteger distributeKeyGenerator = new AtomicInteger();
	/** key=playerId, value=session */
	private ConcurrentMap<Long, IdSession> player2sessions = new ConcurrentHashMap<>();


	public void registerNewPlayer(long playerId, IdSession session) {
		//biding playerId to session
		session.setAttribute(IdSession.ID, playerId);
		this.player2sessions.put(playerId, session);
	}

	/**
	 * get session's playerId
	 * @param session
	 * @return
	 */
	public long getPlayerIdBy(IdSession session) {
		if (session != null) {
			return session.getOwnerId();
		}
		return 0;
	}

	public IdSession getSessionBy(long playerId) {
		return player2sessions.get(playerId);
	}

	/**
	 * get appointed sessionAttr
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttr(IoSession session, AttributeKey attrKey, Class<T> attrType) {
		return (T)session.getAttribute(attrKey, attrType);
	}

	public int getNextSessionId() {
		return this.distributeKeyGenerator.getAndIncrement();
	}

	public String getRemoteIp(IoSession session) {
		return ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
	}

}
