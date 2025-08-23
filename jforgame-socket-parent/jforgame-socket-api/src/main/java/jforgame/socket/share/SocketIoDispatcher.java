package jforgame.socket.share;

/**
 * 消息分发器，服务器，客户端均适用
 */
public interface SocketIoDispatcher {

    /**
     * 会话创建时调用
     *
     * @param session socket session
     */
    void onSessionCreated(IdSession session);

    /**
     * 消息分发
     *
     * @param session 会话
     * @param frame   请求消息 {@link jforgame.socket.share.message.RequestDataFrame}
     */
    void dispatch(IdSession session, Object frame);

    /**
     * 会话关闭时调用
     *
     * @param session socket session
     */
    void onSessionClosed(IdSession session);

    /**
     * 会话异常时调用
     *
     * @param session socket session
     * @param cause   异常
     */
    void exceptionCaught(IdSession session, Throwable cause);
}
