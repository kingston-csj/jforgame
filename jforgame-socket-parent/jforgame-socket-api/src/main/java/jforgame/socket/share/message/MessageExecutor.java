package jforgame.socket.share.message;

import java.lang.reflect.Method;

public interface MessageExecutor {

    Method getMethod();

    Class<?>[] getParams();

    Object getHandler();
}
