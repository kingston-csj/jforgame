package jforgame.server.game.vip.model;

public class VipRight {
	
	private int level;
	
	private int exp;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "VipRight [level=" + level + ", exp=" + exp + "]";
	}
	
}
