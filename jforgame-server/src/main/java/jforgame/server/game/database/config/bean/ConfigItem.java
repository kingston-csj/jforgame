package jforgame.server.game.database.config.bean;

import jforgame.server.game.item.factory.ItemKinds;

public class ConfigItem {

	private int modelId;
	/** {@link ItemKinds}*/
	private int type;

	private String name;

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
