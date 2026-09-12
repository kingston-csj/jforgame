package jforgame.demo.socket;

import jforgame.commons.util.NumberUtil;
import jforgame.socket.core.session.IdSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum SessionManager {

    /**
     * 枚举单例
     */
    INSTANCE;

    /**
     * distributeKey auto generator
     */
    private AtomicInteger distributeKeyGenerator = new AtomicInteger();
    /**
     * key=playerId, value=session
     */
    private ConcurrentMap<Long, IdSession> player2sessions = new ConcurrentHashMap<>();


    public void registerNewPlayer(long playerId, IdSession session) {
        //biding playerId to session
        session.setOwnerId(String.valueOf(playerId));
        this.player2sessions.put(playerId, session);
    }

    /**
     * get session's playerId
     *
     * @param session
     * @return
     */
    public long getPlayerIdBy(IdSession session) {
        if (session != null) {
            return NumberUtil.longValue(session.getOwnerId());
        }
        return 0;
    }

    public IdSession getSessionBy(long playerId) {
        return player2sessions.get(playerId);
    }

    public int getNextSessionId() {
        return this.distributeKeyGenerator.getAndIncrement();
    }

}
