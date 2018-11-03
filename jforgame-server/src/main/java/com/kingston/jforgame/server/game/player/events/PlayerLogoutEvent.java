package com.kingston.jforgame.server.game.player.events;

import com.kingston.jforgame.server.listener.BasePlayerEvent;
import com.kingston.jforgame.server.listener.EventType;

/**
 * 玩家登出事件
 * 
 * @author kingston
 *
 */
public class PlayerLogoutEvent extends BasePlayerEvent {

	public PlayerLogoutEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
