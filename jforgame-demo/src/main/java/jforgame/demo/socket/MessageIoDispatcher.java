package jforgame.demo.socket;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.CommonMessageHandlerRegister;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.PreprocessingMessageHandler;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.support.ClientRequestTask;
import jforgame.threadmodel.dispatch.BaseDispatchTask;

import java.io.IOException;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    MessageFactory messageFactory = GameMessageFactory.getInstance();

    public MessageIoDispatcher(String scanPath) {
        this.handlerRegister = new CommonMessageHandlerRegister(scanPath, messageFactory);
        // 客户端请求消息预处理
        addMessageHandler(new PreprocessingMessageHandler(messageFactory, handlerRegister));

        MessageHandler messageHandler = (session, context) -> {
            Object message = context.getRequest();
            int cmd = GameMessageFactory.getInstance().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                logger.error("message executor missed,  cmd={}", cmd);
                return true;
            }

            int sessionId = (int) session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
            ClientRequestTask task = ClientRequestTask.valueOf(session, sessionId, context);
            // 丢到任务消息队列，不在io线程进行业务处理
            GameServer.getMonitorGameExecutor().accept(task);
            return true;
        };

        addMessageHandler(messageHandler);
    }

    @Override
    public void onSessionCreated(IdSession session) {
        session.setAttribute(SessionProperties.DISTRIBUTE_KEY,
                SessionManager.INSTANCE.getNextSessionId());
    }

    @Override
    public void onSessionClosed(IdSession session) {
        long playerId = SessionManager.INSTANCE.getPlayerIdBy(session);
        if (playerId > 0) {
            logger.info("角色[{}]close session", playerId);
            PlayerEnt player = GameContext.playerManager.get(playerId);
            BaseDispatchTask closeTask = new BaseDispatchTask() {
                @Override
                public void action() {
                    GameContext.playerManager.playerLogout(playerId);
                }
            };
            GameServer.getMonitorGameExecutor().accept(closeTask);
        }
    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {
        if (!(cause instanceof IOException)) {
            logger.error("", cause);
        }
    }

}