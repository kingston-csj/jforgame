package com.kingston.jforgame.server.game.skill.facade;

import com.kingston.jforgame.server.game.player.events.PlayerLevelUpEvent;
import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.annotation.EventHandler;
import com.kingston.jforgame.server.listener.annotation.Listener;

@Listener
public class SkillListener {

	@EventHandler(value=EventType.PLAYER_LEVEL_UP)
	public void onPlayerLevelup(PlayerLevelUpEvent levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
