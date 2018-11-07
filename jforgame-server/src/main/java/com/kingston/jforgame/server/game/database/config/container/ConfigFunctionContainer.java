package com.kingston.jforgame.server.game.database.config.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigFunction;
import com.kingston.jforgame.server.game.function.model.OpenType;
import com.kingston.jforgame.server.logs.LoggerUtils;

public class ConfigFunctionContainer implements Reloadable {

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
