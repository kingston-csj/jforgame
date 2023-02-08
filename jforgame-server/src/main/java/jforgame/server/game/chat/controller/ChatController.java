package jforgame.server.game.chat.controller;

import jforgame.server.game.GameContext;
import jforgame.server.game.chat.message.ReqPrivateChat;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestMapping;

@MessageRoute
public class ChatController {
	
	@RequestMapping
	public void reqExecGm(long playerId, ReqPrivateChat req) {
        GameContext.chatManager.privateChat(req.getReceiverId(), req.getContent());
	}
	

}
