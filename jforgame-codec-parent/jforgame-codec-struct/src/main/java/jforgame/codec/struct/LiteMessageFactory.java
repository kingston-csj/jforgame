package jforgame.codec.struct;

/**
 * 轻量级消息工厂
 * 该接口作为jforgame.socket.share.message#MessageFactory的替代者，目的是不希望引入jforgame-socket-api这种重量级的依赖
 */
public interface LiteMessageFactory {

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
}
