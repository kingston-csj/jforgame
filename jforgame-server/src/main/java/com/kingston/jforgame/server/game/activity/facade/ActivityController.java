package com.kingston.jforgame.server.game.activity.facade;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.activity.message.ReqOpenActivityPanelMessage;

@Controller
public class ActivityController {

	@RequestMapping
	public void reqOpenPanel(long playerId, ReqOpenActivityPanelMessage request) {

	}

}
