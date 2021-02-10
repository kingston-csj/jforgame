
package com.kingston.jforgame.server.client;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.ServerScanPaths;
import com.kingston.jforgame.socket.message.MessageFactory;

/**
 * 客户端模拟器启动程序
 * @author kingston
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		MessageFactory.INSTANCE.initMessagePool(ServerScanPaths.MESSAGE_PATH);
		//读取服务器配置
		ServerConfig.getInstance();

		ClientPlayer robot = new ClientPlayer("MrBug");
		robot.buildConnection();
		robot.login();
		robot.selectedPlayer(10000L);
		
//		ReqGmExecMessage req = new ReqGmExecMessage();
//		req.setCommand("reloadConfig configactivity");
//		robot.sendMessage(req);
//		robot.createNew();
	}

}
