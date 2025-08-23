package jforgame.socket.share;
/**
 * 消息处理接口，用于处理具体的消息
 */
public interface MessageHandler {

    /**
     * 消息处理方法
     * @param session 会话
     * @param frame 消息帧, 即{@link jforgame.socket.share.message.RequestDataFrame}
     * @return true 若返回true，则继续到下一个消息处理节点（如果有的话）;否则，则中断此消息的执行
     * @throws Exception when handling message
     *
     */
    boolean messageReceived(IdSession session, Object frame) throws Exception;

}
