package com.kingston.jforgame.server.listener;

/**
 * 玩家事件抽象类
 */
public abstract class PlayerEvent extends GameEvent {

	/** 玩家id */
	private final long playerId;
	
	public PlayerEvent(EventType evtType, long playerId) {
		super(evtType);
		this.playerId = playerId;
	}
	
	public long getPlayerId() {
		return this.playerId;
	}
}
