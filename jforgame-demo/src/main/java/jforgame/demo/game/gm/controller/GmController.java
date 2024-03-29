package jforgame.demo.game.gm.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.gm.message.ReqGmExec;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class GmController {

	@RequestHandler
	public void reqExecGm(long playerId, ReqGmExec msg) {
        GameContext.gmManager.receiveCommand(playerId, msg.command);
	}
	
}
