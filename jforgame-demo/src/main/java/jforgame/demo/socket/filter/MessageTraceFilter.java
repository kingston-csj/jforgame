package jforgame.demo.socket.filter;

import jforgame.commons.util.JsonUtil;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MessageTraceFilter implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageTraceFilter.class);

    private boolean debug = true;

    @Override
    public boolean messageReceived(IdSession session, RequestContext context) throws Exception {
        Object message = context.getRequest();
        if (debug && traceRequest(message)) {
            logger.error("<<<<<<<<<<[{}]{}={}", session, message.getClass().getSimpleName(), JsonUtil.object2String(message));
        }
        return true;
    }

    private boolean traceRequest(Object message) {
        Set<Class<?>> ignores = new HashSet<>();

        return !ignores.contains(message.getClass());
    }

}
