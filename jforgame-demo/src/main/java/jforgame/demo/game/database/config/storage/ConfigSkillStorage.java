package jforgame.demo.game.database.config.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.demo.db.DbUtils;
import jforgame.demo.game.database.config.Reloadable;
import jforgame.demo.game.database.config.bean.ConfigSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSkillStorage implements Reloadable {

	private Map<Integer, ConfigSkill> skills = new HashMap<>();

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void reload() {
		String sql = "SELECT * FROM configskill";
		try {
			List<ConfigSkill> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigSkill.class);
			skills = datas.stream().collect(Collectors.toMap(ConfigSkill::getId, Function.identity()));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public ConfigSkill getSkillBy(int id) {
		return skills.get(id);
	}
}
