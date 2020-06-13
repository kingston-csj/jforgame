package com.kingston.jforgame.server.game.gm.controller;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.gm.message.ReqGmExec;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExec msg) {
        GameContext.getGmManager().receiveCommand(playerId, msg.command);
	}
	
}
