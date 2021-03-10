package jforgame.server.game.player.model;

public class PlayerProfile {
	
	private long id;
	
	private long accountId;

	private String name;

	/**
	 * 职业
	 */
	private int job;

	private int level;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "PlayerBaseInfo [id=" + id + ", accountId=" + accountId + ", name=" + name + ", job=" + job + ", level="
				+ level + "]";
	}
	

}
