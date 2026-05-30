package jforgame.socket.share.message;

import java.lang.reflect.Method;
/**
 * 消息执行器，用于执行具体的消息处理方法
 */
public interface MessageExecutor {
    /**
     * 消息处理方法
     * @return 消息处理方法
     */
    Method getMethod();
    /**
     * 消息处理方法的参数类型
     * @return 消息处理方法的参数类型
     */
    Class<?>[] getParams();
    /**
     * 消息处理的具体执行者
     * @return 消息处理方法的参数类型
     */
    Object getHandler();
}
