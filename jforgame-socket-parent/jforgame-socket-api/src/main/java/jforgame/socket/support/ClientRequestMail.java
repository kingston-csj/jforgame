package jforgame.socket.support;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.threadmodel.actor.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 若线程模型采用{@link jforgame.threadmodel.actor.ActorSystem}
 * 使用该类将用户的消息请求封装成一个邮件，放到Actor的邮箱里
 */
public class ClientRequestMail extends Mail {


    private static final Logger logger = LoggerFactory.getLogger(ClientRequestMail.class);

    private IdSession session;
    /**
     * message executor
     */
    private MessageExecutor messageExecutor;
    /**
     * arguments passed to the method
     */
    private Object[] params;

    /**
     * 客户端请求的消息
     */
    private Object request;
    /**
     * 客户端请求的消息索引
     */
    private int msgIndex;

    public static ClientRequestMail valueOf(IdSession session, MessageExecutor methodExecutor, Object[] params) {
        ClientRequestMail msgTask = new ClientRequestMail();
        msgTask.session = session;
        msgTask.messageExecutor = methodExecutor;
        msgTask.params = params;
        return msgTask;
    }


    public Object[] getParams() {
        return params;
    }

    public IdSession getSession() {
        return session;
    }


    @Override
    public void action() {
        try {
            MethodCaller methodCaller = MethodHandleUtils.getCaller(messageExecutor.getMethod());
            Object response = methodCaller.invoke(messageExecutor.getHandler(), params);
            if (response != null) {
                // 消息处理器包含消息序号，下发响应将其带上
                session.send(msgIndex, response);
            }
        } catch (Throwable e) {
            logger.error("message task execute failed ", e);
        }
    }
}
