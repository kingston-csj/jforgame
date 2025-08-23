package jforgame.socket.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类提供了一个 {@link SocketIoDispatcher}的骨架实现，
 * 以链式的处理方式增强了 {@link  SocketIoDispatcher#dispatch(IdSession, Object)} 方法，
 * 每个ChainedMessageDispatcher可能有多个消息处理器节点，当消息传递到一个消息处理器时，消息处理器可以选择将消息传递给下一个节点，或者停止消息传递。
 *
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
