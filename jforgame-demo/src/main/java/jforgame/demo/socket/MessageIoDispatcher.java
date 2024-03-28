package jforgame.demo.socket;

import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.CommonMessageHandlerRegister;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.MessageParameterConverter;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.task.BaseGameTask;
import jforgame.socket.share.task.MessageTask;
import jforgame.socket.support.DefaultMessageParameterConverter;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    MessageFactory messageFactory = GameMessageFactory.getInstance();

    private MessageParameterConverter msgParameterConverter= new DefaultMessageParameterConverter(messageFactory);

    public MessageIoDispatcher(String scanPath) {
        this.handlerRegister = new CommonMessageHandlerRegister(scanPath, messageFactory);
        MessageHandler messageHandler = (session, message) -> {
            int cmd = GameMessageFactory.getInstance().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                logger.error("message executor missed,  cmd={}", cmd);
                return true;
            }

            Object[] params = msgParameterConverter.convertToMethodParams(session, cmdExecutor.getParams(), message);
            Object controller = cmdExecutor.getHandler();

            int sessionId = (int) session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
            MessageTask task = MessageTask.valueOf(session, sessionId, controller, cmdExecutor.getMethod(), params);
            task.setRequest(message);
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
            BaseGameTask closeTask = new BaseGameTask() {
                @Override
                public void action() {
                    GameContext.playerManager.playerLogout(playerId);
                }
            };
            GameServer.getMonitorGameExecutor().accept(closeTask);
        }
    }

}