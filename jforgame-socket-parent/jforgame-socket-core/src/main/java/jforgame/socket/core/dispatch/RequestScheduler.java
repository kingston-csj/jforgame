package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * 请求调度器，用于将客户端请求消息<br>投递</br>到具体线程模型。
 * @since v4.0.0
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
