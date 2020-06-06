package com.kingston.jforgame.server.game.activity.controller;

import com.kingston.jforgame.server.game.activity.message.ReqOpenActivityPanel;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class ActivityController {

	@RequestMapping
	public void reqOpenPanel(long playerId, ReqOpenActivityPanel request) {

	}

}
