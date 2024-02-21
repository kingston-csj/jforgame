package jforgame.server.game.cross.ladder.controller;

import jforgame.server.cross.core.server.CrossController;
import jforgame.server.cross.core.server.SCSession;
import jforgame.server.game.cross.ladder.message.G2F_LadderTransfer;
import jforgame.socket.share.annotation.RequestHandler;

@CrossController
public class LadderG2FController {
	
	@RequestHandler
	public void reqApply(SCSession session, G2F_LadderTransfer req) {
		System.out.println("收到游戏服协议<--" + req);
	}

}
