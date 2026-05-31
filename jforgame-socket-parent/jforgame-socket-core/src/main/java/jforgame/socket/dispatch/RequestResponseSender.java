package jforgame.socket.dispatch;

import jforgame.socket.session.IdSession;

/**
 * 请求响应发送器。
 * 当一个路由方法有返回值的时候，框架会自动把这个返回值作为响应发送给客户端（包含index字段）
 * 当返回值是void时，框架将忽略返回结果。
 * 用于定义路由方法返回响应对象后，框架应如何将结果下发给客户端或网关。
 * 当逻辑服使用直连客户端模式，直接使用{@link IdSession#send(int,Object)};
 * 若逻辑服使用网关模式，则需要使用能够适配网关的发送方式，例如包装成携带实际内容的转发消息
 * @since 4.0.0
 */
@FunctionalInterface
public interface RequestResponseSender {

    /**
     * 发送响应
     * @see RequestContext#getRequest()
     * @param session        socket session
     * @param requestContext 请求上下文
     */
    void send(IdSession session, RequestContext requestContext);
}
