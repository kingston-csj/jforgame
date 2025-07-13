package jforgame.demo.game.gm.command;

import java.util.List;

import jforgame.demo.game.database.config.ConfigDataPool;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.gm.message.ResGmResult;

/**
 * 修改配置表的gm
 * 
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
	public ResGmResult execute(PlayerEnt player, List<String> params) {
		String tableName = params.get(0);
		if (ConfigDataPool.getInstance().reload(tableName)) {
			return ResGmResult.buildSuccResult("重载" + tableName + "表成功");
		}
		return ResGmResult.buildFailResult("找不到目标配置表");
	}

}
