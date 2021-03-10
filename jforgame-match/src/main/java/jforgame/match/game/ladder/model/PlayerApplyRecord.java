package jforgame.match.game.ladder.model;

public class PlayerApplyRecord {
	
	private long playerId;
	
	private int fromServerId;
	
	private int score;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getFromServerId() {
		return fromServerId;
	}

	public void setFromServerId(int fromServerId) {
		this.fromServerId = fromServerId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
