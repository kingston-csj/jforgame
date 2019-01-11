package com.kingston.jforgame.server.robot.handler;

import com.kingston.jforgame.server.game.player.message.ResCreateNewPlayerMessage;
import com.kingston.jforgame.server.robot.MessageHandler;
import com.kingston.jforgame.server.robot.RobotSession;
import com.kingston.jforgame.socket.message.Message;

public class CreatePlayerHandler implements MessageHandler {

	@Override
	public void onMessageReceive(RobotSession session, Message message) {
		ResCreateNewPlayerMessage createdNotify = (ResCreateNewPlayerMessage)message;
		session.getPlayer().runAi();
	}

}
