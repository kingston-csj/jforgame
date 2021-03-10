package jforgame.server.game.admin.commands;
import java.util.Map;

import jforgame.server.game.GameContext;
import jforgame.server.game.admin.http.CommandHandler;
import jforgame.server.game.admin.http.HttpCommandHandler;
import jforgame.server.game.admin.http.HttpCommandParams;
import jforgame.server.game.admin.http.HttpCommandResponse;
import jforgame.server.game.admin.http.HttpCommands;

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
