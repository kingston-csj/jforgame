package com.kingston.jforgame.server.game.activity;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.activity.message.ReqOpenActivityPanelMessage;
import com.kingston.jforgame.server.game.database.user.player.Player;

@Controller
public class ActivityController {

	@RequestMapping
	public void reqOpenPanel(Player player, ReqOpenActivityPanelMessage request) {

	}

}
