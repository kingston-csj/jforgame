package jforgame.server.game.gm.command;

import java.util.List;

import jforgame.server.db.DbService;
import jforgame.server.game.database.config.ConfigDataPool;
import jforgame.server.game.database.config.bean.ConfigPlayerLevel;
import jforgame.server.game.database.config.storage.ConfigPlayerLevelStorage;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.game.gm.message.ResGmResult;

/**
 * 修改玩家等级的gm
 * @author kinson
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
		
		DbService.getInstance().insertOrUpdate(player);
		return ResGmResult.buildSuccResult("修改玩家等级成功");
	}

}
