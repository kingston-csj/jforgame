package jforgame.demo.game.skill.facade;

import jforgame.demo.game.player.events.PlayerLevelUpEvent;
import jforgame.demo.listener.EventType;
import jforgame.demo.listener.annotation.EventHandler;
import jforgame.demo.listener.annotation.Listener;

@Listener
public class SkillListener {

	@EventHandler(value= EventType.PLAYER_LEVEL_UP)
	public void onPlayerLevelUp(PlayerLevelUpEvent levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
