package jforgame.socket.support;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.RequestContext;
import jforgame.threadmodel.actor.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 若线程模型采用{@link jforgame.threadmodel.actor.ActorSystem}
 * 使用该类将用户的消息请求封装成一个邮件，当actor被调度时，会执行该邮件的action方法
 */
public class ClientRequestMail extends Mail {


    private static final Logger logger = LoggerFactory.getLogger(ClientRequestMail.class);

    private IdSession session;

    private RequestContext requestContext;

    public static ClientRequestMail valueOf(IdSession session, RequestContext context) {
        ClientRequestMail mail = new ClientRequestMail();
        mail.session = session;
        mail.requestContext = context;
        return mail;
    }

    public IdSession getSession() {
        return session;
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
}
