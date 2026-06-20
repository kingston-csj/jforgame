package jforgame.socket.core.protocol.message;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Default message registration factory
 */
public class DefaultMessageFactory implements MessageFactory {

    /**
     * Mapping from message id to message class
     */
    private final Map<Integer, Class<?>> id2Clazz = new ConcurrentHashMap<>();

    /**
     * Mapping from message class to message id
     */
    private final Map<Class<?>, Integer> clazz2Id = new ConcurrentHashMap<>();

    @Override
    public void registerMessage(int cmd, Class<?> clazz) {
        if (id2Clazz.containsKey(cmd)) {
            throw new IllegalStateException("message meta [" + cmd + "] duplicate！！");
        }
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

    @Override
    public Collection<Class<?>> registeredClassTypes() {
        return new HashSet<>(clazz2Id.keySet());
    }


}
