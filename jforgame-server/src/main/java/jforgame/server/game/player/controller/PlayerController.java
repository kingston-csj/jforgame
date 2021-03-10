package jforgame.server.game.player.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.player.message.req.ReqCreateNewPlayer;
import jforgame.socket.IdSession;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class PlayerController {

	@RequestMapping
	public void reqCreateNewPlayer(IdSession session, ReqCreateNewPlayer req) {
        GameContext.playerManager.createNewPlayer(session, req.getName());
	}

}
