package com.kingston.jforgame.server.game.login.events;

import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.BasePlayerEvent;

public class LoginEvent extends BasePlayerEvent {

	public LoginEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
