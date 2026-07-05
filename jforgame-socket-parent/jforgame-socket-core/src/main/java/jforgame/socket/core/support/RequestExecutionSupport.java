package jforgame.socket.core.support;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.core.dispatch.RequestContext;
import jforgame.socket.core.dispatch.RequestExecutionInterceptor;
import jforgame.socket.core.dispatch.RequestResponseSender;
import jforgame.socket.core.session.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request execution support class, responsible for connecting default call flow, response sending and interceptor chain.
 * @since 4.0.0
 */
final class RequestExecutionSupport {

    private static final Logger logger = LoggerFactory.getLogger(RequestExecutionSupport.class);

    private final RequestResponseSender responseSender;

    private final RequestExecutionInterceptor[] interceptors;

    RequestExecutionSupport(RequestResponseSender responseSender, RequestExecutionInterceptor... interceptors) {
        this.responseSender = responseSender;
        this.interceptors = interceptors == null ? new RequestExecutionInterceptor[0] : interceptors.clone();
    }

    void execute(IdSession session, RequestContext requestContext) {
        try {
            for (RequestExecutionInterceptor interceptor : interceptors) {
                if (!interceptor.beforeExecute(session, requestContext)) {
                    return;
                }
            }

            MethodCaller methodCaller = MethodHandleUtils.getCaller(requestContext.getMethodExecutor().getMethod());
            Object response = methodCaller.invoke(requestContext.getMethodExecutor().getHandler(), requestContext.getParams());
            if (response != null) {
                requestContext.setResponse(response);
                responseSender.send(session, requestContext);
            }

            for (RequestExecutionInterceptor interceptor : interceptors) {
                interceptor.afterExecute(session, requestContext);
            }
        } catch (Throwable e) {
            requestContext.setError(e);
            if (!handleException(session, requestContext, e)) {
                logger.error("message task execute failed ", e);
            }
        }
    }

    private boolean handleException(IdSession session, RequestContext requestContext, Throwable throwable) {
        for (RequestExecutionInterceptor interceptor : interceptors) {
            try {
                if (interceptor.handleException(session, requestContext, throwable)) {
                    return true;
                }
            } catch (Throwable ex) {
                logger.error("request interceptor handle exception failed ", ex);
            }
        }
        return false;
    }
}
