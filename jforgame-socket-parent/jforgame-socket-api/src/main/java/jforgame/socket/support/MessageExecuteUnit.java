package jforgame.socket.support;

import jforgame.socket.share.message.MessageExecutor;

import java.lang.reflect.Method;

public class MessageExecuteUnit implements MessageExecutor {

    /** logic handler method */
    private Method method;
    /** arguments passed to method */
    private Class<?>[] params;
    /** logic controller  */
    private Object handler;

    public static MessageExecuteUnit valueOf(Method method, Class<?>[] params, Object handler) {
        MessageExecuteUnit executor = new MessageExecuteUnit();
        executor.method = method;
        executor.params = params;
        executor.handler = handler;

        return executor;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?>[] getParams() {
        return params;
    }

    @Override
    public Object getHandler() {
        return handler;
    }
}
