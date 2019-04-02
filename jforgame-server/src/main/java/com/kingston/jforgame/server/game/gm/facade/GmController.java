package com.kingston.jforgame.server.game.gm.facade;

import com.kingston.jforgame.server.game.gm.GmManager;
import com.kingston.jforgame.server.game.gm.message.ReqGmExec;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExec msg) {
		GmManager.getInstance().receiveCommand(playerId, msg.command);
	}
	
}
