package jforgame.demo.socket;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.socket.core.dispatch.ChainedMessageDispatcher;
import jforgame.socket.core.registry.CommonMessageHandlerRegister;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.dispatch.MessageHandler;
import jforgame.socket.core.registry.MessageHandlerRegister;
import jforgame.socket.core.dispatch.PreprocessingMessageHandler;
import jforgame.socket.core.protocol.message.MessageFactory;
import jforgame.socket.core.dispatch.RequestScheduler;
import jforgame.socket.core.dispatch.RequestSchedulers;
import jforgame.threadmodel.dispatch.BaseDispatchTask;

import java.io.IOException;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private final MessageHandlerRegister handlerRegister;

    public MessageIoDispatcher(String scanPath) {
        this(scanPath, GameMessageFactory.getInstance(),
                RequestSchedulers.newDispatchScheduler(GameServer.getThreadModel(),
                        (session, context) -> ((Number) session.getAttribute(SessionProperties.DISTRIBUTE_KEY)).longValue()));
    }

    public MessageIoDispatcher(String scanPath, MessageFactory messageFactory, RequestScheduler requestScheduler) {
        this.handlerRegister = new CommonMessageHandlerRegister(scanPath, messageFactory);
        // 客户端请求消息预处理
        addMessageHandler(new PreprocessingMessageHandler(messageFactory, handlerRegister));

        MessageHandler messageHandler = (session, context) -> {
            // 预处理阶段已经完成路由匹配和方法绑定，这里只负责调度到业务线程模型
            requestScheduler.schedule(session, context);
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
            GameServer.getThreadModel().accept(closeTask);
        }
    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {
        if (!(cause instanceof IOException)) {
            logger.error("", cause);
        }
    }

}
