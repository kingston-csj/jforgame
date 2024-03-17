package jforgame.socket.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSocketIoDispatcher implements SocketIoDispatcher {

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
