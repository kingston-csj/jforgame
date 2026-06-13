package jforgame.demo.game.gm.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.gm.message.ReqGmExec;
import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;

@MessageRoute
public class GmController {

	@RequestHandler
	public void reqExecGm(long playerId, ReqGmExec msg) {
        GameContext.gmManager.receiveCommand(playerId, msg.command);
	}
	
}
