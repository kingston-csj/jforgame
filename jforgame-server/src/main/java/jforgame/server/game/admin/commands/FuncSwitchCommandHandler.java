package jforgame.server.game.admin.commands;

import jforgame.server.logs.LoggerSystem;
import jforgame.common.utils.NumberUtil;
import jforgame.server.game.admin.http.CommandHandler;
import jforgame.server.game.admin.http.HttpCommandHandler;
import jforgame.server.game.admin.http.HttpCommandParams;
import jforgame.server.game.admin.http.HttpCommandResponse;
import jforgame.server.game.admin.http.HttpCommands;
import jforgame.server.net.NetGateKeeper;

@CommandHandler(cmd=HttpCommands.FUNC_SWITCH)
public class FuncSwitchCommandHandler extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {

		int messageId = NumberUtil.intValue(httpParams.getInt("messageId"));
		boolean open = NumberUtil.booleanValue(httpParams.getString("switch"));
		if (open) {
			NetGateKeeper.getInstance().openProtocol(messageId);
		} else {
			NetGateKeeper.getInstance().closeProtocol(messageId);
		}

		LoggerSystem.HTTP_COMMAND.getLogger().info("收到后台命令，切换功能{}开关为{}", messageId, open);
		return HttpCommandResponse.valueOfSucc();
	}

}