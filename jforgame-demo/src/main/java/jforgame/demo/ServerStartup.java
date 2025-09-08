package jforgame.demo;

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
