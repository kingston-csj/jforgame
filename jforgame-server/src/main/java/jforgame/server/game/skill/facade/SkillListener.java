package jforgame.server.game.skill.facade;

import jforgame.server.game.player.events.PlayerLevelUpEvent;
import jforgame.server.listener.EventType;
import jforgame.server.listener.annotation.EventHandler;
import jforgame.server.listener.annotation.Listener;

@Listener
public class SkillListener {

	@EventHandler(value= EventType.PLAYER_LEVEL_UP)
	public void onPlayerLevelUp(PlayerLevelUpEvent levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
