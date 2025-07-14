package jforgame.demo;

import jforgame.demo.db.AsyncDbService;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.socket.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * game server start entrance
 */
public class ServerStartup {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    public static void main(String args[]) {
        try {
            GameServer.getInstance().start();
            // test
//            PlayerEnt p = GameContext.playerManager.get(10000L);
//            p.setLevel(666);
//            p.setName("robot22");
////            AsyncDbService.getInstance().deleteFromDb(p);
//            Thread.sleep(3000);
//            PlayerEnt p2 = new PlayerEnt();
//            p2.setId(111L);
//            p2.setName("robot33");
//            AsyncDbService.getInstance().saveToDb(p2);

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
