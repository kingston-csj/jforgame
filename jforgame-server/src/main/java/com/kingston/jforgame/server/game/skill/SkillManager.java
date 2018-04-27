package com.kingston.jforgame.server.game.skill;

import com.kingston.jforgame.server.game.database.config.ConfigDatasPool;
import com.kingston.jforgame.server.game.database.config.bean.ConfigSkill;
import com.kingston.jforgame.server.game.database.config.container.ConfigSkillContainer;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.listener.BaseGameEvent;

public class SkillManager {

	private static volatile SkillManager instance = new SkillManager();

	public static SkillManager getInstance() {
		return instance;
	}

	public void studyNewSkill(Player player, BaseGameEvent event) {
		int skillId = 1;
		ConfigSkill configSkill = getSkillBy(skillId);

		System.err.println(player.getName()+"学会了"+configSkill.getName());
	}

	public ConfigSkill getSkillBy(int id) {
		ConfigSkillContainer container = ConfigDatasPool.getInstance().configSkillContainer;
		return container.getSkillBy(id);
	}

}
