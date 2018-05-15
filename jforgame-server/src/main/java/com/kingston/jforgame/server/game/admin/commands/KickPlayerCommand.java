package com.kingston.jforgame.server.game.admin.commands;
import java.util.Map;

import com.kingston.jforgame.server.game.admin.http.CommandHandler;
import com.kingston.jforgame.server.game.admin.http.HttpCommandHandler;
import com.kingston.jforgame.server.game.admin.http.HttpCommandParams;
import com.kingston.jforgame.server.game.admin.http.HttpCommandResponse;
import com.kingston.jforgame.server.game.admin.http.HttpCommands;
import com.kingston.jforgame.server.game.player.PlayerManager;

@CommandHandler(cmd=HttpCommands.KICK_PLAYER)
public class KickPlayerCommand extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {
		Map<String, String> params = httpParams.getParams();
		String key = "player";
		if (params.containsKey(key)) {
			long playeId = Long.parseLong(params.get(key));
			PlayerManager.getInstance().kickPlayer(playeId);
			return HttpCommandResponse.valueOfSucc();
		}
		return HttpCommandResponse.valueOfSucc();
	}

}
