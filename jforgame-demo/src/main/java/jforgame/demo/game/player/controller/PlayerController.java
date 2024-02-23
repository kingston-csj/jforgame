package jforgame.demo.game.player.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.player.message.req.ReqCreateNewPlayer;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class PlayerController {

	@RequestHandler
	public void reqCreateNewPlayer(IdSession session, ReqCreateNewPlayer req) {
        GameContext.playerManager.createNewPlayer(session, req.getName());
	}

}
