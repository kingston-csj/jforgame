package jforgame.demo.game.player.events;

import jforgame.demo.game.database.user.PlayerEnt;

/**
 * 玩家登出事件
 */
public class PlayerLogoutEvent extends PlayerEvent {

	public PlayerLogoutEvent(PlayerEnt player) {
		super(player);
	}

}
