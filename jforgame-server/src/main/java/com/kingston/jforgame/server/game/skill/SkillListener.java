package com.kingston.jforgame.server.game.skill;

import com.kingston.jforgame.server.game.player.events.EventPlayerLevelUp;
import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.annotation.EventHandler;
import com.kingston.jforgame.server.listener.annotation.Listener;

@Listener
public class SkillListener {
	
	@EventHandler(value=EventType.LEVEL_UP)
	public void onPlayerLevelup(EventPlayerLevelUp levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
