package jforgame.socket.share.message;

import java.util.Collection;

/**
 * 消息工厂，用于注册和获取消息类
 */
public interface MessageFactory {

    /**
     * 注册消息类
     *
     * @param cmd   消息id
     * @param clazz 消息类
     */
    void registerMessage(int cmd, Class<?> clazz);

    /**
     * 通过消息号获取消息类
     *
     * @param cmd 消息id
     * @return 消息类
     */
    Class<?> getMessage(int cmd);

    /**
     * 通过消息类获取消息号
     *
     * @param clazz 消息类
     * @return 消息号
     */
    int getMessageId(Class<?> clazz);

    /**
     * 检查是否包含指定的消息类
     *
     * @param clazz 消息类
     * @return 是否包含
     */
    boolean contains(Class<?> clazz);

    /**
     * 获取所有已注册的消息类
     *
     * @return 所有已注册的消息类
     * @since 1.2.0
     */
    Collection<Class<?>> registeredClassTypes();

}
