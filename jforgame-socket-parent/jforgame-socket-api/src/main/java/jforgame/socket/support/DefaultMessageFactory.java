package jforgame.socket.support;

import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultMessageFactory implements MessageFactory {


    private final Map<Integer, Class<?>> id2Clazz = new HashMap<>();

    private final Map<Class<?>, Integer> clazz2Id = new HashMap<>();

    @Override
    public void registerMessage(int cmd, Class<?> clazz) {
        if (id2Clazz.containsKey(cmd)) {
            throw new IllegalStateException("message meta [" + cmd + "] duplicate！！");
        }
        MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
        if (meta == null) {
            throw new RuntimeException("messages[" + clazz.getSimpleName() + "] missed MessageMeta annotation");
        }
        Logger logger = LoggerFactory.getLogger(DefaultMessageFactory.class);
        logger.debug("register message {} {} ", cmd, clazz.getSimpleName());
        id2Clazz.put(cmd, clazz);
        clazz2Id.put(clazz, cmd);
    }

    @Override
    public Class<?> getMessage(int cmd) {
        return id2Clazz.get(cmd);
    }

    @Override
    public int getMessageId(Class<?> clazz) {
        return clazz2Id.get(clazz);
    }

    @Override
    public boolean contains(Class<?> clazz) {
        return clazz2Id.containsKey(clazz);
    }



}
