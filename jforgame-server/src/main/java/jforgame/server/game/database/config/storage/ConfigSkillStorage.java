package jforgame.server.game.database.config.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.server.db.DbUtils;
import jforgame.server.game.database.config.Reloadable;
import jforgame.server.game.database.config.bean.ConfigSkill;
import jforgame.server.logs.LoggerUtils;

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
