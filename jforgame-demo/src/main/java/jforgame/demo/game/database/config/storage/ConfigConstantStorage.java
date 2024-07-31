package jforgame.demo.game.database.config.storage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jforgame.demo.db.DbUtils;
import jforgame.demo.game.database.config.CommonConfigs;
import jforgame.demo.game.database.config.Reloadable;
import jforgame.demo.game.database.config.bean.ConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigConstantStorage implements Reloadable {

	private Map<Integer, ConfigConstant> configs;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void reload() {
		String sql = "SELECT * FROM configconstant";
		try {
			List<ConfigConstant> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigConstant.class);
			configs = datas.stream().collect(Collectors.toMap(ConfigConstant::getId, Function.identity()));
			// 把数据转为到枚举对象里
			CommonConfigs.initialize(configs);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
