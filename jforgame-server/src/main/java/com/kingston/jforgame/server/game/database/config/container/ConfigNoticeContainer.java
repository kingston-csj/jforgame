package com.kingston.jforgame.server.game.database.config.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigNotice;

public class ConfigNoticeContainer implements Reloadable {

	private Map<Integer, ConfigNotice> messages = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigNotice";
		List<ConfigNotice> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigNotice.class);

		messages = datas.stream().collect(
				Collectors.toMap(ConfigNotice::getId, Function.identity()));
	}

	public ConfigNotice getNoticeBy(int id) {
		return messages.get(id);
	}

}
