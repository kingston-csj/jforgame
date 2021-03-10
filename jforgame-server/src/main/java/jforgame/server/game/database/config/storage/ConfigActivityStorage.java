package jforgame.server.game.database.config.storage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.server.db.DbUtils;
import jforgame.server.game.database.config.Reloadable;
import jforgame.server.game.database.config.bean.ConfigActivity;
import jforgame.server.logs.LoggerUtils;

/**
 * 活动配置
 * 
 * @author kinson
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
