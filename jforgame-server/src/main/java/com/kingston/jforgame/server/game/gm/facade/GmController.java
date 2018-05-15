package com.kingston.jforgame.server.game.gm.facade;

import com.kingston.jforgame.server.game.gm.GmManager;
import com.kingston.jforgame.server.game.gm.message.ReqGmExecMessage;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExecMessage msg) {
		GmManager.getInstance().receiveCommand(playerId, msg.command);
	}
	
}
