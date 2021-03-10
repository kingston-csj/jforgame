package jforgame.server.robot;

import jforgame.server.ServerConfig;
import jforgame.server.ServerScanPaths;
import jforgame.server.client.ClientPlayer;
import jforgame.socket.message.MessageFactory;

public class RobotStartup {

	public static void main(String[] args) {
		// 初始化协议池
		MessageFactory.INSTANCE.initMessagePool(ServerScanPaths.MESSAGE_PATH);
		// 读取服务器配置
		ServerConfig.getInstance();
		
		ClientPlayer firstPlayer = new ClientPlayer("kinson");
		firstPlayer.buildConnection();
		firstPlayer.login();
		firstPlayer.selectedPlayer(10000L);

		for (long start = 1000; start <= 1000; start++) {
			Robot robot = new Robot(start);
			robot.doConnection();
		}
	}

}
