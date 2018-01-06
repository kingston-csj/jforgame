package com.kingston.jforgame.server.game.login.events;

import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.PlayerEvent;

public class LoginEvent extends PlayerEvent {

	public LoginEvent(EventType evtType, long playerId) {
		super(evtType, playerId);
	}

}
