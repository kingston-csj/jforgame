package com.kingston.jforgame.server.game.database.config.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigSkill;
import com.kingston.jforgame.server.logs.LoggerUtils;

public class ConfigSkillStorage implements Reloadable {

	private Map<Integer, ConfigSkill> skills = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigSkill";
		try {
			List<ConfigSkill> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigSkill.class);
			skills = datas.stream().collect(Collectors.toMap(ConfigSkill::getId, Function.identity()));
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public ConfigSkill getSkillBy(int id) {
		return skills.get(id);
	}
}
