package jforgame.demo.socket;

import jforgame.demo.ServerScanPaths;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageFactory;

public class GameMessageFactory implements MessageFactory {

    private static volatile DefaultMessageFactory self;

    public static MessageFactory getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (GameMessageFactory.class) {
            if (self == null) {
                self = new DefaultMessageFactory(ServerScanPaths.MESSAGE_PATH);
            }
            return self;
        }
    }

    @Override
    public void registerMessage(int cmd, Class<?> clazz) {
        self.registerMessage(cmd, clazz);
    }

    @Override
    public Class<?> getMessage(int cmd) {
        return self.getMessage(cmd);
    }

    @Override
    public int getMessageId(Class<?> clazz) {
        return self.getMessageId(clazz);
    }

    @Override
    public boolean contains(Class<?> clazz) {
        return self.contains(clazz);
    }
}
