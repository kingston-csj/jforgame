package jforgame.demo.game.admin.commands;

import jforgame.demo.game.admin.http.CommandHandler;
import jforgame.demo.game.admin.http.HttpCommandHandler;
import jforgame.demo.game.admin.http.HttpCommandParams;
import jforgame.demo.game.admin.http.HttpCommandResponse;
import jforgame.demo.game.admin.http.HttpCommands;
import jforgame.demo.game.core.SchedulerManager;
import jforgame.demo.game.logger.LoggerSystem;

@CommandHandler(cmd = HttpCommands.CLOSE_SERVER)
public class CloseServerCommandHandler extends HttpCommandHandler {

    @Override
    public HttpCommandResponse action(HttpCommandParams httpParams) {
        LoggerSystem.HTTP_COMMAND.getLogger().info("收到后台命令，准备停服");

        SchedulerManager.getInstance().schedule(() -> {
            //发出关闭信号，交由ServerStartup的关闭钩子处理
            Runtime.getRuntime().exit(0);
        }, 5 * 1000);

        return HttpCommandResponse.valueOfSucc();
    }

}
