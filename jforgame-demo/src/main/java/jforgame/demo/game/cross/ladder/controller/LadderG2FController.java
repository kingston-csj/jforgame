package jforgame.demo.game.cross.ladder.controller;

import jforgame.demo.game.cross.ladder.message.G2F_LadderTransfer;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.RequestHandler;

public class LadderG2FController {
	
	@RequestHandler
	public void reqApply(IdSession session, G2F_LadderTransfer req) {
		System.out.println("收到游戏服协议<--" + req);
	}

}
