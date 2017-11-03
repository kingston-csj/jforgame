package com.kingston.game;

import java.util.Map;

import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.database.config.bean.ConfigConstant;
import com.kingston.game.database.config.container.ConfigConstantContainer;

public enum CommonConfigs {
	
	PLAYER_MAX_LEVEL(1){
		public void parseConfig() {
			System.err.println(getStringVaule());
		}
	},
	
	;
	
	private int id;
	
	private int intValue;
	
	private String stringValue;
	
	public static void initialize(Map<Integer, ConfigConstant> configs) {
		for (CommonConfigs config:values()) {
			ConfigConstant configConstant = configs.get(config.id);
			config.intValue = configConstant.getIntValue();
			config.stringValue = configConstant.getStringValue();
			config.parseConfig();
		}
	}
	
	CommonConfigs(int id) {
		this.id = id;
	}
	
	public void parseConfig() {
		
	}
	
	public int getIntValue() {
		return this.intValue;
	}
	
	public String getStringVaule() {
		return this.stringValue;
	}

}
