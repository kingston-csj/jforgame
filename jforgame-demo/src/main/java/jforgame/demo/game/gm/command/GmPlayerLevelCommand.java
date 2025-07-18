package jforgame.demo.game.gm.command;

import java.util.List;

import jforgame.demo.db.AsyncDbService;
import jforgame.demo.game.database.config.ConfigDataPool;
import jforgame.demo.game.database.config.bean.ConfigPlayerLevel;
import jforgame.demo.game.database.config.storage.ConfigPlayerLevelStorage;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.gm.message.ResGmResult;

/**
 * 修改玩家等级的gm
 * 
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
	public ResGmResult execute(PlayerEnt player, List<String> params) {
		int newLevel = Integer.parseInt(params.get(0));
		ConfigPlayerLevelStorage configStorage = ConfigDataPool.getInstance().getStorage(ConfigPlayerLevelStorage.class);
		ConfigPlayerLevel configLevel = configStorage.getConfigBy(newLevel);
		if (configLevel == null) {
			return ResGmResult.buildFailResult("目标等级参数无效");
		}
		player.setLevel(newLevel);
		
		AsyncDbService.getInstance().saveToDb(player);
		return ResGmResult.buildSuccResult("修改玩家等级成功");
	}

}
