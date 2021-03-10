package jforgame.match.game.ladder.model;

/**
 * 匹配结果
 */
public class LadderMatchVo {
	
	private long bluePlayerId;
	
	private String bluePlayerName;
	
	private int blueServerId;
	
	private long redPlayerId;
	
	private String redPlayerName;
	
	private int redServerId;
	
	private String fightServerIp;
	
	private int fightServerPort;

	public long getBluePlayerId() {
		return bluePlayerId;
	}

	public void setBluePlayerId(long bluePlayerId) {
		this.bluePlayerId = bluePlayerId;
	}

	public String getBluePlayerName() {
		return bluePlayerName;
	}

	public void setBluePlayerName(String bluePlayerName) {
		this.bluePlayerName = bluePlayerName;
	}

	public int getBlueServerId() {
		return blueServerId;
	}

	public void setBlueServerId(int blueServerId) {
		this.blueServerId = blueServerId;
	}

	public long getRedPlayerId() {
		return redPlayerId;
	}

	public void setRedPlayerId(long redPlayerId) {
		this.redPlayerId = redPlayerId;
	}

	public String getRedPlayerName() {
		return redPlayerName;
	}

	public void setRedPlayerName(String redPlayerName) {
		this.redPlayerName = redPlayerName;
	}

	public int getRedServerId() {
		return redServerId;
	}

	public void setRedServerId(int redServerId) {
		this.redServerId = redServerId;
	}

	public String getFightServerIp() {
		return fightServerIp;
	}

	public void setFightServerIp(String fightServerIp) {
		this.fightServerIp = fightServerIp;
	}

	public int getFightServerPort() {
		return fightServerPort;
	}

	public void setFightServerPort(int fightServerPort) {
		this.fightServerPort = fightServerPort;
	}

}