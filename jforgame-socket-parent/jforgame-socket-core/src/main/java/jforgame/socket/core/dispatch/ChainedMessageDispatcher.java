package jforgame.socket.core.dispatch;

import jforgame.socket.core.session.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a skeletal implementation of {@link SocketIoDispatcher}.
 * It enhances the {@link SocketIoDispatcher#dispatch(IdSession, RequestContext)} method in a chain pattern.
 * Each ChainedMessageDispatcher may have multiple message handler nodes.
 * When a message is passed to a message handler, the handler can choose to pass the message to the next node or stop the message propagation.
 *
 * @see MessageHandler#messageReceived(IdSession, RequestContext)
 */
public abstract class ChainedMessageDispatcher implements SocketIoDispatcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Chain of message handlers
     * Iterates through handlers in the chain, stops and returns if any handler returns false
     */
    protected List<MessageHandler> dispatchChain = new ArrayList<>();

    /**
     * Adds a message handler to the end of the current chain
     * @param handler message handler
     */
    public void addMessageHandler(MessageHandler handler) {
        this.dispatchChain.add(handler);
    }

    @Override
    public void dispatch(IdSession session, RequestContext context) {
        for (MessageHandler messageHandler : dispatchChain) {
            try {
                if (!messageHandler.messageReceived(session, context)) {
                    break;
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {
        logger.error("", cause);
    }

}
