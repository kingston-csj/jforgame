package com.kingston;

import com.kingston.net.MessageFactory;
import com.kingston.robot.SocketRobot;

public class ClientStarter {

	public static void main(String[] args) {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		
		SocketRobot robot = new SocketRobot("hello");
		robot.buildConnection();
		robot.sendMessage();
	}
}
