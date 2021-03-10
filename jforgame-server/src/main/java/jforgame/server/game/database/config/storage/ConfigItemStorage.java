package jforgame.server.game.database.config.storage;

import java.util.HashMap;
import java.util.Map;

import jforgame.server.game.database.config.Reloadable;
import jforgame.server.game.database.config.bean.ConfigItem;

public class ConfigItemStorage implements Reloadable {

	private Map<Integer, ConfigItem> items = new HashMap<>();

	@Override
	public void reload() {
//		String sql = "SELECT * FROM ConfigItem";
//		try {
//			List<ConfigItem> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigItem.class);
//
//			items = datas.stream().collect(Collectors.toMap(ConfigItem::getModelId, Function.identity()));
//		} catch (Exception e) {
//			LoggerUtils.error("", e);
//		}
	}

	public ConfigItem getItemBy(int modelId) {
		return items.get(modelId);
	}

}
