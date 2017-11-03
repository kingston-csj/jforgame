package com.kingston.game.gm;

import com.kingston.game.gm.message.ReqGmExecMessage;
import com.kingston.net.annotation.Controller;
import com.kingston.net.annotation.RequestMapping;

@Controller
public class GmController {

	@RequestMapping
	public void reqExecGm(long playerId, ReqGmExecMessage msg) {
		GmManager.getInstance().receiveCommand(playerId, msg.command);
	}
	
}
