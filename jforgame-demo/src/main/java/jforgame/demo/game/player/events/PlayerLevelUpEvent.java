package jforgame.demo.game.player.events;

import jforgame.demo.game.database.user.PlayerEnt;

public class PlayerLevelUpEvent extends PlayerEvent {

	/** 新的等级 */
	private final int upLevel;

	public PlayerLevelUpEvent(PlayerEnt player, int newLevel) {
		super(player);
		this.upLevel = newLevel;
	}

	public int getUpLevel() {
		return this.upLevel;
	}

	@Override
	public String toString() {
		return "EventPlayerLevelUp [upLevel=" + upLevel + ", playerId="
				+ getOwner().getId() + "]";
	}

}
