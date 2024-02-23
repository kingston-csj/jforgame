package jforgame.demo.game.database.config.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.demo.db.DbUtils;
import jforgame.demo.game.database.config.Reloadable;
import jforgame.demo.game.database.config.bean.ConfigSkill;
import jforgame.demo.logs.LoggerUtils;

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
