package jforgame.socket.support;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.RequestContext;
import jforgame.threadmodel.dispatch.BaseDispatchTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 若线程模型采用{@link jforgame.threadmodel.dispatch.DispatchThreadModel}
 * 使用该类将用户的消息请求封装成一个命令
 */
public class ClientRequestTask extends BaseDispatchTask {

    private static final Logger logger = LoggerFactory.getLogger(ClientRequestTask.class);

    private IdSession session;

    private RequestContext requestContext;

    public static ClientRequestTask valueOf(IdSession session, long dispatchKey, RequestContext context) {
        ClientRequestTask task = new ClientRequestTask();
        task.dispatchKey = dispatchKey;
        task.session = session;
        task.requestContext = context;
        return task;
    }


    @Override
    public void action() {
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

    @Override
    public String toString() {
        return "[" + requestContext.getMethodExecutor().getHandler().getClass().getName() + "@" + requestContext.getMethodExecutor().getMethod().getName() + "]";
    }

}