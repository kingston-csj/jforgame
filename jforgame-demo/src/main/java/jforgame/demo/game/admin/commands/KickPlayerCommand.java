package jforgame.demo.game.admin.commands;
import java.util.Map;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.admin.http.CommandHandler;
import jforgame.demo.game.admin.http.HttpCommandHandler;
import jforgame.demo.game.admin.http.HttpCommandParams;
import jforgame.demo.game.admin.http.HttpCommandResponse;
import jforgame.demo.game.admin.http.HttpCommands;

@CommandHandler(cmd=HttpCommands.KICK_PLAYER)
public class KickPlayerCommand extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {
		Map<String, String> params = httpParams.getParams();
		String key = "player";
		if (params.containsKey(key)) {
			long playerId = Long.parseLong(params.get(key));
            GameContext.playerManager.kickPlayer(playerId);
			return HttpCommandResponse.valueOfSucc();
		}
		return HttpCommandResponse.valueOfSucc();
	}

}
