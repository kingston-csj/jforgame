package jforgame.server;

import jforgame.server.cross.demo.CrossDemoGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * game server start entrance
 * 
 * @author kinson
 */
public class ServerStartup {

	private static Logger logger = LoggerFactory.getLogger(ServerStartup.class);

	public static void main(String args[]) {
		// vm arguments:
		// -Xms1024m -Xmx1024m -Xmn1024m -XX:MaxTenuringThreshold=3 -XX:+UseG1GC
		// -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime
		// -XX:-OmitStackTraceInFastThrow -XX:+PrintTenuringDistribution
		// -Dcom.sun.management.jmxremote.port=10086
		// -Dcom.sun.management.jmxremote.authenticate=false
		// -Dcom.sun.management.jmxremote.ssl=false

		try {
			GameServer.getInstance().start();
			// test
//			PlayerEnt p = GameContext.playerManager.get(10000L);
//			p.getVipRight().setLevel(123);
//			p.setExp(234);
//			p.setName("robot");
//			DbService.getInstance().saveColumns(p, "vipRight");
//			Thread.sleep(3000);
//			DbService.getInstance().insertOrUpdate(p);

//			CrossDemoGameService.sayHello();
		} catch (Exception e) {
			logger.error("server start failed", e);
			System.exit(-1);
		} finally {
			// add shutdown task
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					GameServer.getInstance().shutdown();
				}
			}));
		}
	}

}
