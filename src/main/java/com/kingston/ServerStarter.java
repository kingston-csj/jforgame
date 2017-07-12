package com.kingston;

import com.kingston.logs.LoggerUtils;
import com.kingston.net.MessageFactory;
import com.kingston.net.SocketServer;
import com.kingston.net.context.TaskHandlerContext;

public class ServerStarter {

	public static void main(String args[]) {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		//读取服务器配置
		ServerConfig.getInstance().initFromConfigFile();
		//初始化消息工作线程池
		TaskHandlerContext.INSTANCE.initialize();
		
		//启动socket服务
		try{
			new SocketServer().start();
		}catch(Exception e) {
			LoggerUtils.error("ServerStarter failed ", e);
		}
	} 

}
