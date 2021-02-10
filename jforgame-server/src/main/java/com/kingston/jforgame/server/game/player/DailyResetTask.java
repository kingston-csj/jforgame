package com.kingston.jforgame.server.game.player;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.database.user.player.Player;

public class DailyResetTask implements Runnable {

	private Player player;

	public DailyResetTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		System.err.println("玩家"+player.getName()+"进行每日重置");
        GameContext.getPlayerManager().checkDailyReset(player);
	}

}
