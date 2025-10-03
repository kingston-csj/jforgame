package jforgame.demo.socket;

import jforgame.commons.util.ClassScanner;
import jforgame.demo.ServerScanPaths;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageFactory;

import java.util.Collection;
import java.util.Set;

public class GameMessageFactory implements MessageFactory {

    private static volatile DefaultMessageFactory self ;

    public static MessageFactory getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (GameMessageFactory.class) {
            if (self == null) {
                self = new DefaultMessageFactory();
                Set<Class<?>> messages = ClassScanner.listClassesWithAnnotation(ServerScanPaths.MESSAGE_PATH, MessageMeta.class);
                for (Class<?> clazz : messages) {
                    MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
                    int key = buildKey(meta.module(), meta.cmd());
                    self.registerMessage(key, clazz);
                }
            }
            return self;
        }
    }

    private static int buildKey(short module, int cmd) {
        int result = Math.abs(module) * 1000 + Math.abs(cmd);
        return cmd < 0 ? -result : result;
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

    @Override
    public Collection<Class<?>> registeredClassTypes() {
        return self.registeredClassTypes();
    }
}