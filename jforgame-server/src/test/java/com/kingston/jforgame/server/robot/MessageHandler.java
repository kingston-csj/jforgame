package com.kingston.jforgame.server.robot;

import com.kingston.jforgame.socket.message.Message;

public interface MessageHandler {

	void onMessageReceive(RobotSession session, Message message);

}
