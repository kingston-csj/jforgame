package com.kingston.game.skill;

import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.database.config.bean.ConfigSkill;
import com.kingston.game.database.config.container.ConfigSkillContainer;
import com.kingston.game.database.user.player.Player;
import com.kingston.listener.EventListener;
import com.kingston.listener.EventType;
import com.kingston.listener.GameEvent;
import com.kingston.listener.annotation.EventHandler;

public class SkillManager implements EventListener {
	
	private static SkillManager instance = new SkillManager();
	
	public SkillManager() {
	}
	
	public static SkillManager getInstance() {
		return instance;
	}
	
	@EventHandler(value=EventType.LEVEL_UP)
	public void studyNewSkill(Player player, GameEvent event) {
		int skillId = 1;
		ConfigSkill configSkill = getSkillBy(skillId);
		
		System.err.println(player.getName()+"学会了"+configSkill.getName());
	}
	
	public ConfigSkill getSkillBy(int id) {
		ConfigSkillContainer container = ConfigDatasPool.getInstance().configSkillContainer;
		return container.getSkillBy(id);
	}

}
