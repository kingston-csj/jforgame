package com.kingston.jforgame.server.game.gm.command;

import java.util.List;

import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.game.database.config.ConfigDatasPool;
import com.kingston.jforgame.server.game.database.config.bean.ConfigPlayerLevel;
import com.kingston.jforgame.server.game.database.config.storage.ConfigPlayerLevelStorage;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.gm.message.ResGmResult;

/**
 * 修改玩家等级的gm
 * @author kingston
 */
public class GmPlayerLevelCommand extends AbstractGmCommand {
	
	@Override
	public String getPattern() {
		return "^playerLv\\s+(\\d+)";
	}

	@Override
	public String help() {
		return "修改玩家等级(playerLv [level])";
	}

	@Override
	public ResGmResult execute(Player player, List<String> params) {
		int newLevel = Integer.parseInt(params.get(0));
		ConfigPlayerLevelStorage configStorage = ConfigDatasPool.getInstance().getStorage(ConfigPlayerLevelStorage.class);
		ConfigPlayerLevel configLevel = configStorage.getConfigBy(newLevel);
		if (configLevel == null) {
			return ResGmResult.buildFailResult("目标等级参数无效");
		}
		player.setLevel(newLevel);
		
		DbService.getInstance().insertOrUpdate(player);
		return ResGmResult.buildSuccResult("修改玩家等级成功");
	}

}
