package com.kingston.game.gm.command;

import java.lang.reflect.Field;
import java.util.List;

import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.database.config.Reloadable;
import com.kingston.game.database.user.player.Player;
import com.kingston.game.gm.message.ResGmResultMessage;

/**
 * 修改配置表的gm
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
		String containerName = tableName + "Container";
		try {
			Field field = ConfigDatasPool.class.getDeclaredField(containerName);
			Class<?> type = field.getType();
			Reloadable newContainer = (Reloadable) type.newInstance();
			newContainer.reload();
			field.set(ConfigDatasPool.getInstance(), newContainer);
			
			return ResGmResultMessage.buildSuccResult("重载["+tableName+"]表成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResGmResultMessage.buildFailResult("找不到目标配置表");
	}

}
