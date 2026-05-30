package jforgame.socket.share;

import jforgame.socket.share.message.MessageExecutor;

/**
 * 消息处理注册器，用于注册消息处理方法
 */
public interface MessageHandlerRegister {
    /**
     * 注册消息处理方法
     *
     * @param cmd      消息cmd
     * @param executor 消息处理方法
     */
    void register(int cmd, MessageExecutor executor);

    /**
     * 获取消息处理方法
     *
     * @param cmd 消息cmd
     * @return 消息处理方法
     */
    MessageExecutor getMessageExecutor(int cmd);

}
