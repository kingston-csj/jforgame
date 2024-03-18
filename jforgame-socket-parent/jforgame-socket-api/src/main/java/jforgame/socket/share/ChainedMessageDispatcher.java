package jforgame.socket.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provide a skeletal implementation of the SocketIoDispatcher interface,
 * to enhance {@link  SocketIoDispatcher#dispatch(IdSession, Object)} method by providing
 * a chained message handler. Each of the ChainedMessageDispatcher may have some {@link MessageHandler} nodes,
 * when the message passed to a MessageHandler, the MessageHandler can choose to pass it to the next node,
 * or stop the message passing.
 * @see MessageHandler#messageReceived(IdSession, Object)
 */
public abstract class ChainedMessageDispatcher implements SocketIoDispatcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected List<MessageHandler> dispatchChain = new ArrayList<>();

    public void addMessageHandler(MessageHandler handler) {
        this.dispatchChain.add(handler);
    }

    @Override
    public void dispatch(IdSession session, Object message) {
        for (MessageHandler messageHandler : dispatchChain) {
            try {
                if (!messageHandler.messageReceived(session, message)) {
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
