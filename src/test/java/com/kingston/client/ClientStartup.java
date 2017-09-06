package com.kingston.client;

import com.kingston.ServerConfig;
import com.kingston.net.MessageFactory;
import com.kingston.robot.SocketRobot;

/**
 * 客户端模拟器启动程序
 * @author kingston
 */
public class ClientStartup {

	public static void main(String[] args) throws Exception {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		//读取服务器配置
		ServerConfig.getInstance().initFromConfigFile();

		SocketRobot robot = new SocketRobot("kingston");
		robot.buildConnection();
		robot.login();
		
	}

}
