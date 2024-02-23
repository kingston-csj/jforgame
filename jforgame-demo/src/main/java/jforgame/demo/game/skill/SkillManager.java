package jforgame.demo.game.skill;

import jforgame.demo.game.database.config.ConfigDataPool;
import jforgame.demo.game.database.config.bean.ConfigSkill;
import jforgame.demo.game.database.config.storage.ConfigSkillStorage;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.listener.BaseGameEvent;

public class SkillManager {

	public void studyNewSkill(PlayerEnt player, BaseGameEvent event) {
		int skillId = 1;
		ConfigSkill configSkill = getSkillBy(skillId);

		System.err.println(player.getName()+"学会了"+configSkill.getName());
	}

	public ConfigSkill getSkillBy(int id) {
		ConfigSkillStorage storage = ConfigDataPool.getInstance().getStorage(ConfigSkillStorage.class);
		return storage.getSkillBy(id);
	}

}
