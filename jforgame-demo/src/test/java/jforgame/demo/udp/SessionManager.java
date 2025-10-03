package jforgame.demo.udp;

import jforgame.commons.util.TimeUtil;
import jforgame.demo.game.core.SchedulerManager;
import jforgame.socket.share.IdSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {

    private static SessionManager inst = new SessionManager();

    private ConcurrentMap<Long, Player> id2Players = new ConcurrentHashMap<>();

    private IdSession serverSession;

    public static SessionManager getInstance() {
        return inst;
    }

    public void register(long playerId, Player player) {
        id2Players.put(playerId, player);
    }

    public void buildSession(IdSession session) {
        serverSession = session;
    }

    public void schedule() {
        SchedulerManager.getInstance().scheduleAtFixedRate(()->{
            id2Players.forEach((key, value) -> {
                ResWelcome push = new ResWelcome();
                push.setTime(System.currentTimeMillis());
                value.receive(serverSession, push);
            });
        }, TimeUtil.MILLIS_PER_MINUTE, TimeUtil.MILLIS_PER_SECOND);
    }

}
