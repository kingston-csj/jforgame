package com.kingston.game.admin.commands;

import java.util.Map;

import com.kingston.game.player.PlayerManager;
import com.kingston.http.CommandHandler;
import com.kingston.http.HttpCommandHandler;
import com.kingston.http.HttpCommandParams;
import com.kingston.http.HttpCommandResponse;
import com.kingston.http.HttpCommands;

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
