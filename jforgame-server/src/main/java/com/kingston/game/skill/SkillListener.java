package com.kingston.game.skill;

import com.kingston.game.player.events.EventPlayerLevelUp;
import com.kingston.listener.EventType;
import com.kingston.listener.annotation.EventHandler;
import com.kingston.listener.annotation.Listener;

@Listener
public class SkillListener {
	
	@EventHandler(value=EventType.LEVEL_UP)
	public void onPlayerLevelup(EventPlayerLevelUp levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
