package jforgame.demo.game.skill.facade;

import jforgame.commons.eventbus.EventSubscriber;
import jforgame.commons.eventbus.Subscribe;
import jforgame.demo.game.player.events.PlayerLevelUpEvent;

@EventSubscriber
public class SkillListener {

	@Subscribe
	public void onPlayerLevelUp(PlayerLevelUpEvent levelUpEvent) {
		System.err.println(getClass().getSimpleName() + "捕捉到事件" + levelUpEvent);
	}

}
