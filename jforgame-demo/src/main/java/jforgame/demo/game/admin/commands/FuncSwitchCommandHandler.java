package jforgame.demo.game.admin.commands;

import jforgame.demo.logs.LoggerSystem;
import jforgame.commons.NumberUtil;
import jforgame.demo.game.admin.http.CommandHandler;
import jforgame.demo.game.admin.http.HttpCommandHandler;
import jforgame.demo.game.admin.http.HttpCommandParams;
import jforgame.demo.game.admin.http.HttpCommandResponse;
import jforgame.demo.game.admin.http.HttpCommands;
import jforgame.demo.socket.NetGateKeeper;

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