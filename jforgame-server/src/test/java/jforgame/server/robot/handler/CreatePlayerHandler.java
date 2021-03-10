package jforgame.server.robot.handler;

import jforgame.server.game.player.message.res.ResCreateNewPlayer;
import jforgame.server.robot.MessageHandler;
import jforgame.server.robot.RobotSession;
import jforgame.socket.message.Message;

public class CreatePlayerHandler implements MessageHandler {

	@Override
	public void onMessageReceive(RobotSession session, Message message) {
		ResCreateNewPlayer createdNotify = (ResCreateNewPlayer)message;
		session.getPlayer().runAi();
	}

}
