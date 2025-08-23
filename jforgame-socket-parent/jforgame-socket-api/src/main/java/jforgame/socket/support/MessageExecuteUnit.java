package jforgame.socket.support;

import jforgame.socket.share.message.MessageExecutor;

import java.lang.reflect.Method;

/**
 * 默认的消息执行单元
 */
public class MessageExecuteUnit implements MessageExecutor {

    /**
     * 对应消息路由的指定方法
     * @see jforgame.socket.share.annotation.MessageRoute
     */
    private Method method;
    /**
     * 对应消息路由的指定方法的参数类型
     */
    private Class<?>[] params;
    /**
     * 对应消息路由对象
     * @see jforgame.socket.share.annotation.MessageRoute
     */
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
