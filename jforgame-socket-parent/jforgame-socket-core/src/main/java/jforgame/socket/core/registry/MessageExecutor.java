package jforgame.socket.core.registry;

import java.lang.reflect.Method;
/**
 * Message executor, for executing specific message handling methods
 */
public interface MessageExecutor {
    /**
     * Message handling method
     * @return message handling method
     */
    Method getMethod();
    /**
     * Parameter types of message handling method
     * @return parameter types of message handling method
     */
    Class<?>[] getParams();
    /**
     * The actual executor of message handling
     * @return the handler
     */
    Object getHandler();
}
