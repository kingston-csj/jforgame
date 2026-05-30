package jforgame.socket.share;

/**
 * 请求调度器，用于将客户端请求消息投递到具体线程模型。
 */
@FunctionalInterface
public interface RequestScheduler {

    /**
     * 调度请求
     *
     * @param session socket session
     * @param context 请求上下文
     */
    void schedule(IdSession session, RequestContext context);
}
