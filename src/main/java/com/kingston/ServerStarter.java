package com.kingston;

import com.kingston.net.MessageFactory;
import com.kingston.net.SocketServer;

public class ServerStarter {

	public static void main(String args[]) {
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		try{
			new SocketServer().start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 

}
