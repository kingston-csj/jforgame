package com.kingston.game.http.commands;

import com.kingston.game.http.CommandHandler;
import com.kingston.game.http.HttpCommandHandler;
import com.kingston.game.http.HttpCommandParams;
import com.kingston.game.http.HttpCommandResponse;
import com.kingston.game.http.HttpCommands;
import com.kingston.logs.LoggerSystem;
import com.kingston.utils.SchedulerManager;

@CommandHandler(cmd=HttpCommands.CLOSE_SERVER)
public class CloseServerCommandHandler extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {
		LoggerSystem.HTTP_COMMAND.getLogger().info("收到后台命令，准备停服");

		SchedulerManager.INSTANCE.registerUniqueTimeoutTask("http_close_server", () -> {
			//发出关闭信号，交由ServerStartup的关闭钩子处理
			Runtime.getRuntime().exit(0);
		},  5*1000);

		return HttpCommandResponse.valueOfSucc();
	}

}
