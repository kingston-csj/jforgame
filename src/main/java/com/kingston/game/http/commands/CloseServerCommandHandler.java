package com.kingston.game.http.commands;

import com.kingston.GameServer;
import com.kingston.game.http.CommandHandler;
import com.kingston.game.http.HttpCommandHandler;
import com.kingston.game.http.HttpCommandParams;
import com.kingston.game.http.HttpCommandResponse;
import com.kingston.game.http.HttpCommands;

@CommandHandler(cmd=HttpCommands.CLOSE_SERVER)
public class CloseServerCommandHandler extends HttpCommandHandler {

	@Override
	public HttpCommandResponse action(HttpCommandParams httpParams) {
		GameServer.getInstance().shutdown();
		return HttpCommandResponse.valueOfSucc();
	}

}
