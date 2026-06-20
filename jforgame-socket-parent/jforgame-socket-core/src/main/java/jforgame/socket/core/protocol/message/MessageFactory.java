package jforgame.socket.core.protocol.message;

import java.util.Collection;

/**
 * Message factory, for registering and retrieving message classes
 */
public interface MessageFactory {

    /**
     * Register message class
     *
     * @param cmd   message id
     * @param clazz message class
     */
    void registerMessage(int cmd, Class<?> clazz);

    /**
     * Get message class by message id
     *
     * @param cmd message id
     * @return message class
     */
    Class<?> getMessage(int cmd);

    /**
     * Get message id by message class
     *
     * @param clazz message class
     * @return message id
     */
    int getMessageId(Class<?> clazz);

    /**
     * Check if the specified message class is contained
     *
     * @param clazz message class
     * @return true if contained
     */
    boolean contains(Class<?> clazz);

    /**
     * Get all registered message classes
     *
     * @return all registered message classes
     * @since 1.2.0
     */
    Collection<Class<?>> registeredClassTypes();

}
