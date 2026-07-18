package jforgame.demo.udp;

import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.GameServer;
import jforgame.socket.core.dispatch.ChainedMessageDispatcher;
import jforgame.socket.core.registry.CommonMessageHandlerRegister;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.MessageHandler;
import jforgame.socket.core.dispatch.RequestScheduler;
import jforgame.socket.core.registry.MessageHandlerRegister;
import jforgame.socket.core.registry.MessageExecutor;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.dispatch.RequestSchedulers;

import java.util.Collections;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    MessageFactory messageFactory = GameMessageFactory.getInstance();

    private final RequestScheduler requestScheduler;

    public MessageIoDispatcher() {
        LoginRouter router = new LoginRouter();
        this.handlerRegister = new CommonMessageHandlerRegister(Collections.singletonList(router), messageFactory);
        this.requestScheduler = RequestSchedulers.newDispatchScheduler(GameServer.getThreadModel(), (context) -> context.getSession().hashCode());
        MessageHandler messageHandler = (session, context) -> {
            Object message = context.getRequest();
            int cmd = GameMessageFactory.getInstance().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                logger.error("message executor missed,  cmd={}", cmd);
                return true;
            }

            // 丢到任务消息队列，不在io线程进行业务处理
            requestScheduler.schedule(session, context);
            return true;
        };

        addMessageHandler(messageHandler);
    }

    @Override
    public void onSessionCreated(IdSession session) {
    }

    @Override
    public void onSessionClosed(IdSession session) {
    }

}
