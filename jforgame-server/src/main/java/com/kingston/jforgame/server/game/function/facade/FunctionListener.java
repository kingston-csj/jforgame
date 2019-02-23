package com.kingston.jforgame.server.game.function.facade;

import java.util.List;
import java.util.Set;

import com.kingston.jforgame.server.game.database.config.ConfigDatasPool;
import com.kingston.jforgame.server.game.database.config.bean.ConfigFunction;
import com.kingston.jforgame.server.game.database.config.storage.ConfigFunctionStorage;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.function.model.OpenType;
import com.kingston.jforgame.server.game.player.PlayerManager;
import com.kingston.jforgame.server.game.player.events.PlayerLevelUpEvent;
import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.annotation.EventHandler;
import com.kingston.jforgame.server.listener.annotation.Listener;

@Listener
public class FunctionListener {

	@EventHandler(value=EventType.PLAYER_LEVEL_UP)
	public void onPlayerLevelup(PlayerLevelUpEvent levelUpEvent) {
		long playerId = levelUpEvent.getPlayerId();
		Player player = PlayerManager.getInstance().get(playerId);
		Set<Integer> openFuncs = player.getFunction().getFuncs();

		ConfigFunctionStorage functionStorage = ConfigDatasPool.getInstance().getStorage(ConfigFunctionStorage.class);
		List<ConfigFunction> openByLevelFuncs = functionStorage.getFunctionBy(OpenType.LEVEL);

		int level = player.getLevel();
		for (ConfigFunction configFunc : openByLevelFuncs) {
			int funcId = configFunc.getId();
			if (! openFuncs.contains(funcId)) {
				if (level >= configFunc.getOpenTarget()) {
					player.getFunction().open(funcId);
				}
			}
		}
	}

}
