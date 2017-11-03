package com.kingston.game.http.commands;

import java.util.Map;

import com.kingston.game.http.CommandHandler;
import com.kingston.game.http.HttpCommandHandler;
import com.kingston.game.http.HttpCommandParams;
import com.kingston.game.http.HttpCommandResponse;
import com.kingston.game.http.HttpCommands;
import com.kingston.game.player.PlayerManager;

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
