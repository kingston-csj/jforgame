package jforgame.socket.core.registry;

import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;

import java.lang.reflect.Method;

/**
 * 默认的消息执行器
 */
public class MessageHandlerMethod implements MessageExecutor {

    /**
     * 对应消息路由的指定方法 {@link RequestHandler}
     * @see MessageRoute
     */
    private Method method;
    /**
     * 对应消息路由的指定方法的参数类型
     */
    private Class<?>[] params;
    /**
     * 对应消息路由对象
     * @see MessageRoute
     */
    private Object handler;

    public static MessageHandlerMethod valueOf(Method method, Class<?>[] params, Object handler) {
        MessageHandlerMethod executor = new MessageHandlerMethod();
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
