package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;

/**
 * Request execution interceptor.
 * Used to extend the execution process of a request in the business thread, can add logging, monitoring, authentication before and after method calls,
 * or handle exceptions uniformly.
 * @since 4.0.0
 */
public interface RequestExecutionInterceptor {

    /**
     * Callback before request method execution.
     *
     * @param session        socket session
     * @param requestContext request context
     */
    default void beforeExecute(IdSession session, RequestContext requestContext) {
    }

    /**
     * Callback after request method execution completes.
     * <p>
     * If the handling method returns a response object, the response has been written back to {@code requestContext},
     * and the default response dispatch logic has also been executed.
     *
     * @param session        socket session
     * @param requestContext request context
     */
    default void afterExecute(IdSession session, RequestContext requestContext) {
    }

    /**
     * Handles exceptions thrown during execution.
     *
     * @param session        socket session
     * @param requestContext request context
     * @param throwable      exception object
     * @return {@code true} means the exception has been handled, framework will not print default error logs
     */
    default boolean handleException(IdSession session, RequestContext requestContext, Throwable throwable) {
        return false;
    }
}
