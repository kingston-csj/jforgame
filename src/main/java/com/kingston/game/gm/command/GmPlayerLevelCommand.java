package com.kingston.game.gm.command;

import java.util.List;

import com.kingston.db.DbService;
import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.database.config.bean.ConfigPlayerLevel;
import com.kingston.game.database.user.player.Player;
import com.kingston.game.gm.message.ResGmResultMessage;

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
	public ResGmResultMessage execute(Player player, List<String> params) {
		int newLevel = Integer.parseInt(params.get(0));
		ConfigPlayerLevel configLevel = ConfigDatasPool.getInstance()
								.configPlayerLevelContainer.getConfigBy(newLevel);
		if (configLevel == null) {
			return ResGmResultMessage.buildFailResult("目标等级参数无效");
		}
		player.setLevel(newLevel);
		player.setUpdate();
		
		DbService.getInstance().add2Queue(player);
		return ResGmResultMessage.buildSuccResult("修改玩家等级成功");
	}

}
