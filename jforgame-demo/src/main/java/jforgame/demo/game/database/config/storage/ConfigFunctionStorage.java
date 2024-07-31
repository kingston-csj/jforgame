package jforgame.demo.game.database.config.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.demo.db.DbUtils;
import jforgame.demo.game.database.config.Reloadable;
import jforgame.demo.game.database.config.bean.ConfigFunction;
import jforgame.demo.game.function.model.OpenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFunctionStorage implements Reloadable {

	private Map<Integer, ConfigFunction> functions = new HashMap<>();

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void reload() {
		String sql = "SELECT * FROM configfunction";
		try {
			List<ConfigFunction> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigFunction.class);

			functions = datas.stream().collect(Collectors.toMap(ConfigFunction::getId, Function.identity()));
		} catch (Exception e) {
			logger.error("", e);
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
