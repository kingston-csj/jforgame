package jforgame.socket.share.task;

import jforgame.socket.share.IdSession;
import jforgame.socket.client.Traceable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * 将用户的消息请求封装成一个命令
 */
public class MessageTask extends BaseGameTask {

    private static Logger logger = LoggerFactory.getLogger(MessageTask.class);

    private IdSession session;

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
    /**
     * request message
     */
    private Object request;

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
            if (response != null) {
                session.send(response);
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

	public Object getRequest() {
		return request;
	}

	public void setRequest(Object request) {
		this.request = request;
	}

	@Override
    public String toString() {
        return "[" + handler.getClass().getName() + "@" + method.getName() + "]";
    }

}
