package jforgame.demo.game.player.events;

import jforgame.commons.eventbus.BaseEvent;
import jforgame.demo.game.database.user.PlayerEnt;

/**
 * 玩家事件抽象类
 */
public abstract class PlayerEvent implements BaseEvent {

    private final PlayerEnt player;

    public PlayerEvent(PlayerEnt player) {
        this.player = player;
    }

    @Override
    public PlayerEnt getOwner() {
        return player;
    }
}
