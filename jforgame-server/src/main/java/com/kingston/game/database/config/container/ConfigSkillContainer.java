package com.kingston.game.database.config.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.game.database.config.Reloadable;
import com.kingston.game.database.config.bean.ConfigSkill;
import com.kingston.orm.utils.DbUtils;

public class ConfigSkillContainer implements Reloadable{

	private Map<Integer, ConfigSkill> skills = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigSkill";
		List<ConfigSkill> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigSkill.class);
		skills = datas.stream().collect(
				Collectors.toMap(ConfigSkill::getId, Function.identity()));
		
	}
	
	public ConfigSkill getSkillBy(int id) {
		return skills.get(id);
	}
}
