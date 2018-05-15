package com.kingston.jforgame.server.game.admin.commands;

import com.kingston.jforgame.common.utils.NumberUtil;
import com.kingston.jforgame.server.game.admin.http.CommandHandler;
import com.kingston.jforgame.server.game.admin.http.HttpCommandHandler;
import com.kingston.jforgame.server.game.admin.http.HttpCommandParams;
import com.kingston.jforgame.server.game.admin.http.HttpCommandResponse;
import com.kingston.jforgame.server.game.admin.http.HttpCommands;
import com.kingston.jforgame.server.logs.LoggerSystem;
import com.kingston.jforgame.server.net.NetGateKeeper;

@CommandHandler(cmd=HttpCommands.FUNC_SWITCH)
public class FuncSwitchCommandHandler extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {

		int messageId = NumberUtil.intValue(httpParams.getInt("messageId"));
		boolean open = NumberUtil.booleanValue(httpParams.getString("switch"));
		if (open) {
			NetGateKeeper.getInstance().openProcotol(messageId);
		} else {
			NetGateKeeper.getInstance().closeProcotol(messageId);
		}

		LoggerSystem.HTTP_COMMAND.getLogger().info("收到后台命令，切换功能{}开关为{}", messageId, open);
		return HttpCommandResponse.valueOfSucc();
	}

}