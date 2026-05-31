package jforgame.socket.dispatch;

import jforgame.socket.session.IdSession;

/**
 * 请求响应发送器。
 * <p>
 * 用于定义路由方法返回响应对象后，框架应如何将结果下发给客户端或网关。
 */
@FunctionalInterface
public interface RequestResponseSender {

    /**
     * 发送响应
     *
     * @param session socket session
     * @param requestContext 请求上下文
     * @param response 响应对象
     */
    void send(IdSession session, RequestContext requestContext, Object response);
}
