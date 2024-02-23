package jforgame.demo.game.player.events;

import jforgame.demo.listener.BasePlayerEvent;
import jforgame.demo.listener.EventType;

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
