package jforgame.demo.game.player;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;

public class DailyResetTask implements Runnable {

	private PlayerEnt player;

	public DailyResetTask(PlayerEnt player) {
		this.player = player;
	}

	@Override
	public void run() {
		System.err.println("玩家"+player.getName()+"进行每日重置");
        GameContext.playerManager.checkDailyReset(player);
	}

}
