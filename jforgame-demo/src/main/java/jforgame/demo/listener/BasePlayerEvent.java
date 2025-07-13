package jforgame.demo.listener;

/**
 * 玩家事件抽象类
 */
public abstract class BasePlayerEvent extends BaseGameEvent {

    /**
     * 玩家id
     */
    private final long playerId;

    public BasePlayerEvent(EventType evtType, long playerId) {
        super(evtType);
        this.playerId = playerId;
    }

    public long getPlayerId() {
        return this.playerId;
    }
}
