package jforgame.demo.game.database.config.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.demo.db.DbUtils;
import jforgame.demo.game.database.config.Reloadable;
import jforgame.demo.game.database.config.bean.ConfigNotice;
import jforgame.demo.logs.LoggerUtils;

public class ConfigNoticeStorage implements Reloadable {

	private Map<Integer, ConfigNotice> messages = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigNotice";
		try {
			List<ConfigNotice> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigNotice.class);

			messages = datas.stream().collect(Collectors.toMap(ConfigNotice::getId, Function.identity()));
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public ConfigNotice getNoticeBy(int id) {
		return messages.get(id);
	}

}
