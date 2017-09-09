package com.kingston.game.database.config.container;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kingston.game.CommonConfigs;
import com.kingston.game.database.config.Reloadable;
import com.kingston.game.database.config.bean.ConfigConstant;
import com.kingston.orm.utils.DbUtils;

public class ConfigConstantContainer implements Reloadable {
	
	private Map<Integer, ConfigConstant> configs;

	@Override
	public void reload() {
		String sql = "SELECT * FROM ConfigConstant";
		List<ConfigConstant> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigConstant.class);
		configs = datas.stream().collect(
				Collectors.toMap(ConfigConstant::getId, Function.identity()));
		
		//把数据转为到枚举对象里
		CommonConfigs.initialize(configs);
		
	}
	

}
