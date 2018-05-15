package com.kingston.jforgame.server.game.activity.facade;

import com.kingston.jforgame.server.game.activity.message.ReqOpenActivityPanelMessage;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class ActivityController {

	@RequestMapping
	public void reqOpenPanel(long playerId, ReqOpenActivityPanelMessage request) {

	}

}
