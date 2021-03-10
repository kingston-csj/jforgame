package jforgame.server.game.player.model;

import java.util.ArrayList;
import java.util.List;

public class AccountProfile {
	
	private long accountId;
	
	private List<PlayerProfile> players = new ArrayList<>();
	
	private long recentPlayer;

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public List<PlayerProfile> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerProfile> players) {
		this.players = players;
	}

	public long getRecentPlayer() {
		return recentPlayer;
	}

	public void setRecentPlayer(long recentPlayer) {
		this.recentPlayer = recentPlayer;
	}
	
	public void addPlayerProfile(PlayerProfile player) {
		this.players.add(player);
	}

	@Override
	public String toString() {
		return "AccountProfile [accountId=" + accountId + ", players=" + players + ", recentPlayer=" + recentPlayer
				+ "]";
	}
	
}
