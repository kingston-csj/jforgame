package com.kingston.jforgame.server.game.gm;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.gm.message.ReqGmExecMessage;

@Controller
public class GmFacade {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExecMessage msg) {
		GmManager.getInstance().receiveCommand(playerId, msg.command);
	}
	
}
