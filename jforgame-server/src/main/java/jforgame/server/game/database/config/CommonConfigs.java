package jforgame.server.game.database.config;

import java.util.Map;

import jforgame.server.game.database.config.bean.ConfigConstant;

public enum CommonConfigs {

	/** 角色解封的最大等级 */
	PLAYER_MAX_LEVEL(1){
		@Override
		public void parseConfig() {
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
