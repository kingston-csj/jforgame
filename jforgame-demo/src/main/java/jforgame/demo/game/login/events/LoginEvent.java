package jforgame.demo.game.login.events;

import jforgame.demo.listener.EventType;
import jforgame.demo.listener.BasePlayerEvent;

public class LoginEvent extends BasePlayerEvent {

	public LoginEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
