package jforgame.server.game.login.events;

import jforgame.server.listener.EventType;
import jforgame.server.listener.BasePlayerEvent;

public class LoginEvent extends BasePlayerEvent {

	public LoginEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
