package com.kingston.jforgame.server.game.admin.commands;

import com.kingston.jforgame.server.game.admin.http.*;
import com.kingston.jforgame.server.logs.LoggerSystem;
import com.kingston.jforgame.server.thread.SchedulerManager;

@CommandHandler(cmd = HttpCommands.CLOSE_SERVER)
public class CloseServerCommandHandler extends HttpCommandHandler {

    @Override
    public HttpCommandResponse action(HttpCommandParams httpParams) {
        LoggerSystem.HTTP_COMMAND.getLogger().info("收到后台命令，准备停服");

        SchedulerManager.schedule(() -> {
            //发出关闭信号，交由ServerStartup的关闭钩子处理
            Runtime.getRuntime().exit(0);
        }, 5 * 1000);

        return HttpCommandResponse.valueOfSucc();
    }

}
