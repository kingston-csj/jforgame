package jforgame.socket.support;

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
        ClientRequestExecutor.execute(session, requestContext, logger);
    }

    @Override
    public String toString() {
        return ClientRequestExecutor.describe(requestContext);
    }

}
