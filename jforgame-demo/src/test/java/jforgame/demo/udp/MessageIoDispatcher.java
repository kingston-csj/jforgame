package jforgame.demo.udp;

import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.GameServer;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.CommonMessageHandlerRegister;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.MessageParameterConverter;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.RequestDataFrame;
import jforgame.socket.share.task.MessageTask;
import jforgame.socket.support.DefaultMessageParameterConverter;

import java.util.Collections;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    MessageFactory messageFactory = GameMessageFactory.getInstance();

    private MessageParameterConverter msgParameterConverter= new DefaultMessageParameterConverter(messageFactory);

    public MessageIoDispatcher() {
        LoginRouter router = new LoginRouter();
        this.handlerRegister = new CommonMessageHandlerRegister(Collections.singletonList(router), messageFactory);
        MessageHandler messageHandler = (session, frame) -> {
            RequestDataFrame dataFrame = (RequestDataFrame)frame;
            Object message = dataFrame.getMessage();
            int cmd = GameMessageFactory.getInstance().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                logger.error("message executor missed,  cmd={}", cmd);
                return true;
            }

            Object[] params = msgParameterConverter.convertToMethodParams(session, cmdExecutor.getParams(), dataFrame);
            Object controller = cmdExecutor.getHandler();

            MessageTask task = MessageTask.valueOf(session, session.hashCode(), controller, cmdExecutor.getMethod(), params);
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