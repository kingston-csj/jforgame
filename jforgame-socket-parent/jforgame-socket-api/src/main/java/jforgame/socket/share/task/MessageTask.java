package jforgame.socket.share.task;

import jforgame.commons.reflection.MethodCaller;
import jforgame.commons.reflection.MethodHandleUtils;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.support.MessageExecuteUnit;
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
     * message executor
     */
    private MessageExecutor methodExecutor;

    /**
     * MethodHandle调用器，方法句柄实现
     */
    private MethodCaller methodCaller;
    /**
     * arguments passed to the method
     */
    private Object[] params;

    public static MessageTask valueOf(IdSession session, long dispatchKey, MessageExecutor methodExecutor, Object[] params) {
        MessageTask msgTask = new MessageTask();
        msgTask.dispatchKey = dispatchKey;
        msgTask.session = session;
        msgTask.methodExecutor = methodExecutor;
        msgTask.params = params;
        msgTask.methodCaller = MethodHandleUtils.getCaller(methodExecutor.getMethod());
        return msgTask;
    }

    public static MessageTask valueOf(IdSession session, long dispatchKey, Object handler,
                                      Method method, Object[] params) {
        MessageTask msgTask = new MessageTask();
        msgTask.dispatchKey = dispatchKey;
        msgTask.session = session;
        MessageExecutor methodExecutor = MessageExecuteUnit.valueOf(method, null, handler);
        msgTask.params = params;
        msgTask.methodExecutor = methodExecutor;

        return msgTask;
    }

    @Override
    public void action() {
        try {
            Object response = methodCaller.invoke(methodExecutor.getHandler(), params);
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
        return methodExecutor.getHandler();
    }

    public Method getMethod() {
        return methodExecutor.getMethod();
    }

    public Object[] getParams() {
        return params;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    @Override
    public String toString() {
        return "[" + getHandler().getClass().getName() + "@" + getMethod().getName() + "]";
    }

}