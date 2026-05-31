package jforgame.socket.dispatch;

import jforgame.socket.session.IdSession;

/**
 * 请求执行拦截器
 * 用于扩展一次请求在业务线程中的执行过程，可在方法调用前后追加日志、监控、鉴权，
 * 或在异常发生时进行统一处理。
 * @since 4.0.0
 */
public interface RequestExecutionInterceptor {

    /**
     * 在请求方法执行前回调。
     *
     * @param session        socket session
     * @param requestContext 请求上下文
     */
    default void beforeExecute(IdSession session, RequestContext requestContext) {
    }

    /**
     * 在请求方法执行完成后回调。
     * <p>
     * 如果处理方法返回了响应对象，则此时响应已经写回到 {@code requestContext}，
     * 且默认响应下发逻辑也已经执行完成。
     *
     * @param session        socket session
     * @param requestContext 请求上下文
     */
    default void afterExecute(IdSession session, RequestContext requestContext) {
    }

    /**
     * 处理执行阶段抛出的异常。
     *
     * @param session        socket session
     * @param requestContext 请求上下文
     * @param throwable      异常对象
     * @return 返回 {@code true} 表示异常已被处理，框架不再打印默认错误日志
     */
    default boolean handleException(IdSession session, RequestContext requestContext, Throwable throwable) {
        return false;
    }
}
