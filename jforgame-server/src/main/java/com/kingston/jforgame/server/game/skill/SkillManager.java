package com.kingston.jforgame.server.game.skill;

import com.kingston.jforgame.server.game.database.config.ConfigDataPool;
import com.kingston.jforgame.server.game.database.config.bean.ConfigSkill;
import com.kingston.jforgame.server.game.database.config.storage.ConfigSkillStorage;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.listener.BaseGameEvent;

public class SkillManager {

	public void studyNewSkill(Player player, BaseGameEvent event) {
		int skillId = 1;
		ConfigSkill configSkill = getSkillBy(skillId);

		System.err.println(player.getName()+"学会了"+configSkill.getName());
	}

	public ConfigSkill getSkillBy(int id) {
		ConfigSkillStorage storage = ConfigDataPool.getInstance().getStorage(ConfigSkillStorage.class);
		return storage.getSkillBy(id);
	}

}
