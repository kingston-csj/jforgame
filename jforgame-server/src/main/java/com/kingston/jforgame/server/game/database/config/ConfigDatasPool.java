package com.kingston.jforgame.server.game.database.config;

import java.lang.reflect.Field;

import com.kingston.jforgame.server.game.database.config.container.ConfigConstantContainer;
import com.kingston.jforgame.server.game.database.config.container.ConfigPlayerLevelContainer;
import com.kingston.jforgame.server.game.database.config.container.ConfigSkillContainer;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 所有策划配置的数据池
 * @author kingston
 */
public class ConfigDatasPool {
	
	private static ConfigDatasPool instance = new ConfigDatasPool(); 
	
	private ConfigDatasPool() {}
	
	public static ConfigDatasPool getInstance() {
		return instance;
	}
	
	public ConfigPlayerLevelContainer configPlayerLevelContainer = new ConfigPlayerLevelContainer();

	public ConfigSkillContainer configSkillContainer = new ConfigSkillContainer();
	
	public ConfigConstantContainer configConstantContainer = new ConfigConstantContainer();
	
	/**
	 * 起服读取所有的配置数据
	 */
	public void loadAllConfigs() {
		Field[] fields = ConfigDatasPool.class.getDeclaredFields();
		ConfigDatasPool instance = getInstance();
		for (Field f:fields) {
			try {
			if (Reloadable.class.isAssignableFrom(f.getType())) {
				Reloadable container = (Reloadable) f.getType().newInstance();
				container.reload();
				f.set(instance, container);
			}
			}catch (Exception e) {
				LoggerUtils.error("策划配置数据有误，请检查", e);
				System.exit(0);
			}
		}
		
	}
	
	
}
