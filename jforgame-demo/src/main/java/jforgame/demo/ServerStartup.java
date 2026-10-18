package jforgame.demo;

import jforgame.demo.socket.GameServer;
import jforgame.logger.LoggerBuilder;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * game server start entrance
 */
public class ServerStartup {

    static {
        // The application's hook closes Log4j2 after all business shutdown logging is complete.
        // Set this before SLF4J creates the first Log4j2 context and registers its default hook.
        System.setProperty("log4j.shutdownHookEnabled", "false");
        // 强迫初始化日志
        LoggerBuilder.initialize();
    }

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    public static void main(String[] args) {
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
                    try {
                        GameServer.getInstance().shutdown();
                    } finally {
                        LogManager.shutdown();
                    }
                }
            }));
        }
    }

}
