package jforgame.demo.game.activity.controller;

import jforgame.demo.game.activity.message.ReqOpenActivityPanel;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class ActivityController {

	@RequestHandler
	public void reqOpenPanel(long playerId, ReqOpenActivityPanel request) {

	}

}
