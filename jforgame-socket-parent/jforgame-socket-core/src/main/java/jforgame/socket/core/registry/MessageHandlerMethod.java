package jforgame.socket.core.registry;

import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;

import java.lang.reflect.Method;

/**
 * Default message executor
 */
public class MessageHandlerMethod implements MessageExecutor {

    /**
     * Corresponding method annotated with {@link RequestHandler}
     * @see MessageRoute
     */
    private Method method;
    /**
     * Parameter types of the corresponding method
     */
    private Class<?>[] params;
    /**
     * Corresponding message route object
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
