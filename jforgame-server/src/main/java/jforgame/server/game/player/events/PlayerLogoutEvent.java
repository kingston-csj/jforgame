package jforgame.server.game.player.events;

import jforgame.server.listener.BasePlayerEvent;
import jforgame.server.listener.EventType;

/**
 * 玩家登出事件
 * 
 * @author kinson
 *
 */
public class PlayerLogoutEvent extends BasePlayerEvent {

	public PlayerLogoutEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
