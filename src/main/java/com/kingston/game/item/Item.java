package com.kingston.game.item;

import com.kingston.game.database.config.bean.ConfigItem;
import com.kingston.utils.IdGenerator;

public abstract class Item {

	private long id;
	/** {@link ConfigItem#getModelId()} */
	private int modelId;
	/** owned count */
	private int count;
	/** Returns <tt>true</tt> if it's binding item　*/
	private boolean bind;

	public Item() {
		this.id = IdGenerator.getNextId();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isBind() {
		return bind;
	}

	public void setBind(boolean bind) {
		this.bind = bind;
	}

}
