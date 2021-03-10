package jforgame.server.robot;

import jforgame.socket.message.Message;

public interface MessageHandler {

	void onMessageReceive(RobotSession session, Message message);

}
