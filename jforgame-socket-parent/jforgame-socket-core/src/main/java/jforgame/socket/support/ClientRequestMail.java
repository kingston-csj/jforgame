package jforgame.socket.support;

import jforgame.socket.dispatch.RequestContext;
import jforgame.socket.session.IdSession;
import jforgame.threadmodel.actor.mail.Mail;

/**
 * 若线程模型采用{@link jforgame.threadmodel.actor.ActorSystem}
 * 使用该类将用户的消息请求封装成一个邮件，当actor被调度时，会执行该邮件的action方法
 */
public class ClientRequestMail extends Mail {

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
        ClientRequestExecutor.execute(session, requestContext);
    }
}
