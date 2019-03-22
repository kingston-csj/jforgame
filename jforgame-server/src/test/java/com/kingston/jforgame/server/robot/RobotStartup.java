package com.kingston.jforgame.server.robot;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.ServerScanPaths;
import com.kingston.jforgame.server.client.ClientPlayer;
import com.kingston.jforgame.socket.message.MessageFactory;

public class RobotStartup {

	public static void main(String[] args) {
		// 初始化协议池
		MessageFactory.INSTANCE.initMeesagePool(ServerScanPaths.MESSAGE_PATH);
		// 读取服务器配置
		ServerConfig.getInstance();
		
		ClientPlayer firstPlayer = new ClientPlayer("kingston");
		firstPlayer.buildConnection();
		firstPlayer.login();
		firstPlayer.selectedPlayer(10000L);

		for (long start = 1000; start <= 1000; start++) {
			Robot robot = new Robot(start);
			robot.doConnection();
		}
	}

}
