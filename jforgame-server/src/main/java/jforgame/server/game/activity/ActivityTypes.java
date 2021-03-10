package jforgame.server.game.activity;

public enum ActivityTypes {
	
	/** 首充活动 */
	FIRST_CHARGE(1),
	
	;
	
	private int type;
	
	ActivityTypes(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}

}
