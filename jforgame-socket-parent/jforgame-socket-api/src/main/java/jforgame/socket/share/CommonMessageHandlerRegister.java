package jforgame.socket.share;

import jforgame.commons.ClassScanner;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.DefaultMessageHandlerRegister;
import jforgame.socket.support.MessageExecuteUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class CommonMessageHandlerRegister extends DefaultMessageHandlerRegister {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private MessageFactory messageFactory;

    /**
     * 扫描路径动态创建MessageRoute实例，用于非spring环境
     * @param scanPath path to scan class with Annotation {@link MessageRoute}
     * @param messageFactory factory of message
     */
    public CommonMessageHandlerRegister(String scanPath, MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation(scanPath,
                MessageRoute.class);
        for (Class<?> controller : controllers) {
            try {
                Object handler = controller.newInstance();
                register(handler);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    private void register(Object route) {
        Method[] methods = route.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RequestHandler mapperAnnotation = method.getAnnotation(RequestHandler.class);
            if (mapperAnnotation != null) {
                Optional<Class<?>> msgMeta = getMessageMeta(method);
                msgMeta.ifPresent(c->{
                    int cmd = messageFactory.getMessageId(c);
                    MessageExecutor cmdExecutor = getMessageExecutor(cmd);
                    if (cmdExecutor != null) {
                        throw new RuntimeException(String.format("cmd[%d] duplicated", cmd));
                    }
                    cmdExecutor = MessageExecuteUnit.valueOf(method, method.getParameterTypes(), route);
                    register(cmd, cmdExecutor);
                });
            }
        }
    }

    /**
     * 根据MessageRoute实例集合进行注册，用于spring环境
     * @param routes message routes
     * @param messageFactory  factory of message
     */
    public CommonMessageHandlerRegister(Collection<Object>  routes, MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        routes.forEach(this::register);
    }

    private Optional<Class<?>> getMessageMeta(Method method) {
        return Arrays.stream(method.getParameterTypes()).filter(c->c.isAnnotationPresent(MessageMeta.class)).findAny();
    }
}
