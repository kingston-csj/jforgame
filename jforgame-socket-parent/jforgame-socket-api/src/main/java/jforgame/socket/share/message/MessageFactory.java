package jforgame.socket.share.message;

import java.util.Collection;

public interface MessageFactory {


    /**
     * binding  messageId and messageClass
     * if messageId repeated, an IllegalStateException exception thrown
     *
     * @param cmd cmd of the message
     * @param clazz class type of the message
     */
    void registerMessage(int cmd, Class<?> clazz);

    /**
     * return message class of the appointed messageId
     *
     * @param cmd cmd of the message
     * @return target message class of the cmd
     */
    Class<?> getMessage(int cmd);

    /**
     * return id of the message
     *
     * @param clazz target message class
     * @return cmd of the message class
     */
    int getMessageId(Class<?> clazz);

    boolean contains(Class<?> clazz);


    /**
     * return all registered class
     * @since 1.2.0
     * @return a copied view  of the registered class
     */
    Collection<Class<?>> registeredClassTypes();

}
