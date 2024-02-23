package jforgame.demo.game.player.events;

import jforgame.demo.listener.EventType;
import jforgame.demo.listener.BasePlayerEvent;

public class PlayerLevelUpEvent extends BasePlayerEvent {

	/** 新的等级 */
	private int upLevel;
	
	public PlayerLevelUpEvent(EventType evtType, long playerId, int newLevel) {
		super(evtType, playerId);
		this.upLevel = newLevel;
	}
	
	public int getUpLevel() {
		return this.upLevel;
	}

	@Override
	public String toString() {
		return "EventPlayerLevelUp [upLevel=" + upLevel + ", playerId="
				+ getPlayerId() + ", EventType=" + getEventType() + "]";
	}
	
	
}
