package jforgame.demo.game.player.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.player.message.ReqCreateNewPlayer;
import jforgame.socket.session.IdSession;
import jforgame.socket.protocol.annotation.MessageRoute;
import jforgame.socket.protocol.annotation.RequestHandler;

@MessageRoute
public class PlayerController {

	@RequestHandler
	public void reqCreateNewPlayer(IdSession session, ReqCreateNewPlayer req) {
        GameContext.playerManager.createNewPlayer(session, req.getName());
	}

}
