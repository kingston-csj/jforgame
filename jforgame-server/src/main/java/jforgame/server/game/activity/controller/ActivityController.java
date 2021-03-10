package jforgame.server.game.activity.controller;

import jforgame.server.game.activity.message.ReqOpenActivityPanel;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class ActivityController {

	@RequestMapping
	public void reqOpenPanel(long playerId, ReqOpenActivityPanel request) {

	}

}
