package jforgame.socket.support;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.RequestContext;
import org.slf4j.Logger;

/**
 * 请求执行器，复用 ClientRequestTask / ClientRequestMail 的公共执行逻辑。
 */
public final class ClientRequestExecutor {

    private ClientRequestExecutor() {
    }

    public static void execute(IdSession session, RequestContext requestContext, Logger logger) {
        try {
            MethodCaller methodCaller = MethodHandleUtils.getCaller(requestContext.getMethodExecutor().getMethod());
            Object response = methodCaller.invoke(requestContext.getMethodExecutor().getHandler(), requestContext.getParams());
            if (response != null) {
                requestContext.setResponse(response);
                // 消息处理器包含消息序号，下发响应将其带上
                session.send(requestContext.getHeader().getIndex(), response);
            }
        } catch (Throwable e) {
            requestContext.setError(e);
            logger.error("message task execute failed ", e);
        }
    }

    public static String describe(RequestContext requestContext) {
        return "[" + requestContext.getMethodExecutor().getHandler().getClass().getName()
                + "@" + requestContext.getMethodExecutor().getMethod().getName() + "]";
    }
}
