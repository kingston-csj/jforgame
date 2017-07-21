package com.kingston;

import com.kingston.net.MessageFactory;
import com.kingston.robot.SocketRobot;

public class ClientStarter {

	public static void main(String[] args) {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		//读取服务器配置
		ServerConfig.getInstance().initFromConfigFile();

		SocketRobot robot = new SocketRobot("kingston");
		robot.buildConnection();
		robot.sendMessage();
		
	}

}
