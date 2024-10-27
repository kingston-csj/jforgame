package jforgame.socket.share.task;

import jforgame.socket.share.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * 将用户的消息请求封装成一个命令
 */
public class MessageTask extends BaseGameTask {

    private static final Logger logger = LoggerFactory.getLogger(MessageTask.class);

    private IdSession session;

    /**
     * 消息流水号
     */
    private int msgIndex;

    /**
     * message controller
     */
    private Object handler;
    /**
     * target method of the controller
     */
    private Method method;
    /**
     * arguments passed to the method
     */
    private Object[] params;

    public static MessageTask valueOf(IdSession session, long dispatchKey, Object handler,
                                      Method method, Object[] params) {
        MessageTask msgTask = new MessageTask();
        msgTask.dispatchKey = dispatchKey;
        msgTask.session = session;
        msgTask.handler = handler;
        msgTask.method = method;
        msgTask.params = params;

        return msgTask;
    }

    @Override
    public void action() {
        try {
            Object response = method.invoke(handler, params);
            // 如果消息签名带有返回值，则无需index字段，这里会自动把index附加到返回值
            // index字段只用于异步推送才是必填字段
            if (response != null) {
                // 消息处理器包含消息序号，则下发响应将其带上
                session.send(msgIndex, response);
            }
        } catch (Exception e) {
            logger.error("message task execute failed ", e);
        }
    }

    public Object getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    @Override
    public String toString() {
        return "[" + handler.getClass().getName() + "@" + method.getName() + "]";
    }

}
