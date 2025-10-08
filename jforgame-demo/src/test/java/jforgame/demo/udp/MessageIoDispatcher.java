package jforgame.demo.udp;

import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.GameServer;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.CommonMessageHandlerRegister;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.ClientRequestTask;

import java.util.Collections;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    MessageFactory messageFactory = GameMessageFactory.getInstance();

    public MessageIoDispatcher() {
        LoginRouter router = new LoginRouter();
        this.handlerRegister = new CommonMessageHandlerRegister(Collections.singletonList(router), messageFactory);
        MessageHandler messageHandler = (session, context) -> {
            Object message = context.getRequest();
            int cmd = GameMessageFactory.getInstance().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                logger.error("message executor missed,  cmd={}", cmd);
                return true;
            }

            ClientRequestTask task = ClientRequestTask.valueOf(session, session.hashCode(), context);
            // 丢到任务消息队列，不在io线程进行业务处理
            GameServer.getMonitorGameExecutor().accept(task);
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