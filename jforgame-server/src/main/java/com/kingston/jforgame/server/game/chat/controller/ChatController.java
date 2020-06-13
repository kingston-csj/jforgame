package com.kingston.jforgame.server.game.chat.controller;

import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.chat.message.ReqPrivateChat;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class ChatController {
	
	@RequestMapping
	public void reqExecGm(long playerId, ReqPrivateChat req) {
		GameContext.getChatManager().privateChat(req.getReceiverId(), req.getContent());
	}
	

}
