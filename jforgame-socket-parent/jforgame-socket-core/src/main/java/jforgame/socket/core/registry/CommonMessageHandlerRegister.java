package jforgame.socket.core.registry;

import jforgame.commons.util.ClassScanner;
import jforgame.socket.core.protocol.annotation.MessageMeta;
import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;
import jforgame.socket.core.protocol.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Common message handler register, used to register message handling methods
 */
public class CommonMessageHandlerRegister extends DefaultMessageHandlerRegister {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private MessageFactory messageFactory;

    /**
     * Scan path to dynamically create MessageRoute instances, for non-spring environment
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
                    cmdExecutor = MessageHandlerMethod.valueOf(method, method.getParameterTypes(), route);
                    register(cmd, cmdExecutor);
                });
            }
        }
    }

    /**
     * Register based on MessageRoute instance collection, for spring environment.
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
