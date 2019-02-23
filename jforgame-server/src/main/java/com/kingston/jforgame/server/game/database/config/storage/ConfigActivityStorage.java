package com.kingston.jforgame.server.game.database.config.storage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigActivity;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 活动配置
 * 
 * @author kingston
 */
public class ConfigActivityStorage implements Reloadable {

	private Map<Integer, ConfigActivity> activities = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigActivity";
		List<ConfigActivity> datas;
		try {
			datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigActivity.class);

			activities = datas.stream().collect(Collectors.toMap(ConfigActivity::getId, Function.identity()));
		} catch (SQLException e) {
			LoggerUtils.error("", e);
		}

	}

	public ConfigActivity getConfigActivityBy(int id) {
		return activities.get(id);
	}

}
