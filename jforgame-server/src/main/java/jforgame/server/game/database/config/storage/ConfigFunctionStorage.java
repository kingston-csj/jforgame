package jforgame.server.game.database.config.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.server.db.DbUtils;
import jforgame.server.game.database.config.Reloadable;
import jforgame.server.game.database.config.bean.ConfigFunction;
import jforgame.server.game.function.model.OpenType;
import jforgame.server.logs.LoggerUtils;

public class ConfigFunctionStorage implements Reloadable {

	private Map<Integer, ConfigFunction> functions = new HashMap<>();

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigFunction";
		try {
			List<ConfigFunction> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigFunction.class);

			functions = datas.stream().collect(Collectors.toMap(ConfigFunction::getId, Function.identity()));
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	public ConfigFunction getFunctionBy(int id) {
		return functions.get(id);
	}

	public List<ConfigFunction> getFunctionBy(OpenType openType) {
		List<ConfigFunction> result = new ArrayList<>();
		for (Map.Entry<Integer, ConfigFunction> entry : functions.entrySet()) {
			ConfigFunction function = entry.getValue();
			if (function.getOpenType() == openType) {
				result.add(function);
			}
		}

		return result;
	}

}
