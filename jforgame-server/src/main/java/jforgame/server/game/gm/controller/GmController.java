package jforgame.server.game.gm.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.gm.message.ReqGmExec;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExec msg) {
        GameContext.gmManager.receiveCommand(playerId, msg.command);
	}
	
}
