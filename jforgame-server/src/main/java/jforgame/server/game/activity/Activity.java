package jforgame.server.game.activity;

/**
 * 活动数据载体
 * @author kinson
 */
public abstract class Activity {
	
	/** 活动id */
	private int id;
	/** 活动类型 {@link ActivityTypes#getType()} */
	private int type;
	/** 当前是否开放 */
	private boolean opened;
	
	public Activity(int type, int id) {
		this.type = type;
		this.id   = id;
	}
	
	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}
	
	public String getSerializeKey() {
		return this.type + "_" + this.id;
	}
	
	protected String serializeTostring() {
		return null;
	}
	
	public void setOpened(boolean open) {
		this.opened = open;
	}
	
	public boolean isOpened() {
		return this.opened;
	}

}
