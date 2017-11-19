package com.kingston.jforgame.server.game.player;

import com.kingston.jforgame.net.socket.task.TimerTask;
import com.kingston.jforgame.server.game.database.user.player.Player;

public class DailyResetTask extends TimerTask {

	private Player player;

	public DailyResetTask(int distributeKey, Player player) {
		super(distributeKey);
		this.player = player;
	}

	@Override
	public void action() {
		System.err.println("玩家"+player.getName()+"进行每日重置");
		PlayerManager.getInstance().checkDailyReset(player);
	}

}
