package jforgame.demo.game.chat.controller;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.chat.message.ReqPrivateChat;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class ChatController {
	
	@RequestHandler
	public void reqExecGm(long playerId, ReqPrivateChat req) {
        GameContext.chatManager.privateChat(req.getReceiverId(), req.getContent());
	}
	

}
