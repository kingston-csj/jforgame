package jforgame.server.game.chat.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.chat.message.ReqPrivateChat;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class ChatController {
	
	@RequestMapping
	public void reqExecGm(long playerId, ReqPrivateChat req) {
        GameContext.chatManager.privateChat(req.getReceiverId(), req.getContent());
	}
	

}
