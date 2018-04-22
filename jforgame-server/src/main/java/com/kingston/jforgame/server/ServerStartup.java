package com.kingston.jforgame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * game server start entrance
 * @author kingston
 */
public class ServerStartup {

	private static Logger logger = LoggerFactory.getLogger(ServerStartup.class);

	public static void main(String args[]) {
		// vm arguments:
		// -Xms1024m -Xmx1024m -Xmn512m -XX:MaxTenuringThreshold=3 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=2
		// -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime -XX:-OmitStackTraceInFastThrow -XX:+PrintTenuringDistribution
		// -Dcom.sun.management.jmxremote.port=10086 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

		try{
			GameServer.getInstance().start();
		}catch(Exception e){
			logger.error("server start failed", e);
		}finally {
			//add shutdown task
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					GameServer.getInstance().shutdown();
				}
			}));
		}
	}

}
