package com.kingston;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端启动程序
 * @author kingston
 */
public class ServerStartup {

	private static Logger logger = LoggerFactory.getLogger(ServerStartup.class);

	public static void main(String args[]) {

		try{
			GameServer.getInstance().start();
		}catch(Exception e){
			logger.error("服务启动报错", e);
		}finally {
			//增加关闭钩子
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					GameServer.getInstance().shutdown();
				}
			}));
		}
	} 

}
