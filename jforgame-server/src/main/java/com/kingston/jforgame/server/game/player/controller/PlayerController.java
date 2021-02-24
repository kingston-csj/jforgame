package com.kingston.jforgame.server.game.player.controller;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.player.message.req.ReqCreateNewPlayer;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class PlayerController {

	@RequestMapping
	public void reqCreateNewPlayer(IdSession session, ReqCreateNewPlayer req) {
        GameContext.playerManager.createNewPlayer(session, req.getName());
	}

}
