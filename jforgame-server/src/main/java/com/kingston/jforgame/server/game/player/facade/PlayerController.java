package com.kingston.jforgame.server.game.player.facade;

import com.kingston.jforgame.server.game.player.PlayerManager;
import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class PlayerController {

	@RequestMapping
	public void reqCreateNewPlayer(IdSession session, ReqCreateNewPlayerMessage req) {
		PlayerManager.getInstance().createNewPlayer(session, req.getName());
	}

}
