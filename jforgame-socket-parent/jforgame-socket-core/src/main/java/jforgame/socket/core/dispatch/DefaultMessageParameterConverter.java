package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.protocol.message.MessageFactory;

/**
 * Default message parameter converter
 */
public class DefaultMessageParameterConverter implements MessageParameterConverter {

    private MessageFactory messageFactory;

    public DefaultMessageParameterConverter(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, RequestContext context) {
        Object message = context.getRequest();
        Object[] result = new Object[methodParams == null ? 0 : methodParams.length];
        // Method signature with two parameters: method(IdSession session, Object message);
        // Method signature with three parameters: method(IdSession session, int index, Object message);
        for (int i = 0; i < result.length; i++) {
            Class<?> param = methodParams[i];
            if (i == 0) {
                if (IdSession.class.isAssignableFrom(param)) {
                    result[i] = session;
                } else {
                    throw new IllegalArgumentException("message (" + message.getClass().getSimpleName() + ") handler 1st argument must be IdSession");
                }
            }
            if (result.length == 2 && i == 1) {
                if (messageFactory.contains(message.getClass())) {
                    result[i] = message;
                } else {
                    throw new IllegalArgumentException("message (" + message.getClass().getSimpleName() + ") handler 2nd argument must be registered Message");
                }
            }
            if (result.length == 3) {
                if (i == 1) {
                    if (int.class.isAssignableFrom(param) || Integer.class.isAssignableFrom(param)) {
                        result[i] = context.getHeader().getIndex();
                    } else {
                        throw new IllegalArgumentException("2nd argument must be int");
                    }
                }
                if (i == 2) {
                    if (messageFactory.contains(message.getClass())) {
                        result[i] = message;
                    } else {
                        throw new IllegalArgumentException("message (" + message.getClass().getSimpleName() + ") handler 3nd argument must be registered Message");
                    }
                }
            }
        }

        return result;
    }
}
