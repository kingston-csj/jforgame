package com.kingston.jforgame.server.robot.handler;

import com.kingston.jforgame.server.game.player.message.ReqCreateNewPlayerMessage;
import com.kingston.jforgame.server.robot.MessageHandler;
import com.kingston.jforgame.server.robot.RobotSession;
import com.kingston.jforgame.socket.message.Message;

public class LoginSuccHandler implements MessageHandler {

	@Override
	public void onMessageReceive(RobotSession session, Message message) {
		ReqCreateNewPlayerMessage req = new ReqCreateNewPlayerMessage();
		req.setName(session.getPlayer().getName());
		session.sendMessage(req);
	}
}
