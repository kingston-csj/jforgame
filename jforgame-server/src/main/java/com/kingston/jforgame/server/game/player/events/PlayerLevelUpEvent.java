package com.kingston.jforgame.server.game.player.events;

import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.PlayerEvent;

public class PlayerLevelUpEvent extends PlayerEvent {

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
