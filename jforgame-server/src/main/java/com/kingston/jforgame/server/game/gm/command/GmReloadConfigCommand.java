package com.kingston.jforgame.server.game.gm.command;

import java.util.List;

import com.kingston.jforgame.server.game.database.config.ConfigDatasPool;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.gm.message.ResGmResultMessage;

/**
 * 修改配置表的gm
 * 
 * @author kingston
 */
public class GmReloadConfigCommand extends AbstractGmCommand {

	@Override
	public String getPattern() {
		return "^reloadConfig\\s+([a-zA-Z_]+)";
	}

	@Override
	public String help() {
		return "修改配置表(^reloadConfig [tableName])";
	}

	@Override
	public ResGmResultMessage execute(Player player, List<String> params) {
		String tableName = params.get(0);
		if (ConfigDatasPool.getInstance().reload(tableName)) {
			return ResGmResultMessage.buildSuccResult("重载" + tableName + "表成功");
		}
		return ResGmResultMessage.buildFailResult("找不到目标配置表");
	}

}
