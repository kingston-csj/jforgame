package com.kingston.game.database.config.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.game.database.config.Reloadable;
import com.kingston.game.database.config.bean.ConfigPlayerLevel;
import com.kingston.orm.utils.DbUtils;

/**
 * 玩家等级配置表
 * @author kingston
 */
public class ConfigPlayerLevelContainer implements Reloadable{
	
	private Map<Integer, ConfigPlayerLevel> levels = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigPlayerLevel";
		List<ConfigPlayerLevel> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigPlayerLevel.class);
		//使用jdk8，将list转为map
		levels = datas.stream().collect(
				Collectors.toMap(ConfigPlayerLevel::getLevel, Function.identity()));
	}
	
	public ConfigPlayerLevel getConfigBy(int level) {
		return levels.get(level);
	}
	
}
