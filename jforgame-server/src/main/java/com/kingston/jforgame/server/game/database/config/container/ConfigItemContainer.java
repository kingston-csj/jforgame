package com.kingston.jforgame.server.game.database.config.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigItem;
import com.kingston.jforgame.server.logs.LoggerUtils;

public class ConfigItemContainer implements Reloadable {

	private Map<Integer, ConfigItem> items = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigItem";
		try {
			List<ConfigItem> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigItem.class);

			items = datas.stream().collect(Collectors.toMap(ConfigItem::getModelId, Function.identity()));
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public ConfigItem getItemBy(int modelId) {
		return items.get(modelId);
	}

}
