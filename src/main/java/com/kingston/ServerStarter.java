package com.kingston;

import com.kingston.logs.LoggerSystem;
import com.kingston.net.MessageFactory;
import com.kingston.net.SocketServer;

public class ServerStarter {

	public static void main(String args[]) {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		//读取服务配置
		ServerConfig.getInstance().initFromConfigFile();
		
		//启动socket服务
		try{
			new SocketServer().start();
		}catch(Exception e) {
			LoggerSystem.EXCEPTION.getLogger().error("ServerStarter failed ", e);
		}
	} 

}
