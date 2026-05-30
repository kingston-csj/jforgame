package jforgame.socket.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类提供了一个 {@link SocketIoDispatcher}的骨架实现，
 * 以链式的处理方式增强了 {@link  SocketIoDispatcher#dispatch(IdSession, RequestContext)} 方法，
 * 每个ChainedMessageDispatcher可能有多个消息处理器节点，当消息传递到一个消息处理器时，消息处理器可以选择将消息传递给下一个节点，或者停止消息传递。
 *
 * @see MessageHandler#messageReceived(IdSession, RequestContext)
 */
public abstract class ChainedMessageDispatcher implements SocketIoDispatcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 链式消息处理器
     * 遍历链中的消息处理器，若其中一个消息处理器返回false则停止消息传递，并返回
     */
    protected List<MessageHandler> dispatchChain = new ArrayList<>();

    /**
     * 在当前链的结尾添加一个消息处理器
     * @param handler 消息处理器
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
