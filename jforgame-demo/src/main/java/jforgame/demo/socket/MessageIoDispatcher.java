package jforgame.demo.socket;

import jforgame.commons.ClassScanner;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.socket.client.Traceable;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.task.BaseGameTask;
import jforgame.socket.share.task.MessageTask;
import jforgame.socket.support.DefaultMessageFactory;
import jforgame.socket.support.DefaultMessageHandlerRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageIoDispatcher implements SocketIoDispatcher {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * [module_cmd, CmdExecutor]
     */
    private  Map<Integer, MessageExecutor> cmdHandlers = new HashMap<>();

    private List<MessageHandler> dispatchChain = new ArrayList<>();

    public MessageIoDispatcher(String scanPath) {
        initialize(scanPath);

        MessageHandler messageHandler = new MessageHandler() {
            @Override
            public boolean messageReceived(IdSession session, Object message) {
                int cmd = DefaultMessageFactory.getInstance().getMessageId(message.getClass());
                MessageExecutor cmdExecutor = cmdHandlers.get(cmd);
                if (cmdExecutor == null) {
                    logger.error("message executor missed,  cmd={}", cmd);
                    return true;
                }

                Object[] params = convertToMethodParams(session, cmdExecutor.getParams(), message);
                Object controller = cmdExecutor.getHandler();

                int sessionId = (int) session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
                MessageTask task = MessageTask.valueOf(session, sessionId, controller, cmdExecutor.getMethod(), params);
                task.setRequest(message);
                // 丢到任务消息队列，不在io线程进行业务处理
                GameExecutor.getInstance().acceptTask(task);
                return true;
            }
        };
        dispatchChain.add(messageHandler);
    }

    private void initialize(String scanPath) {
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation(scanPath,
                MessageRoute.class);
        this.cmdHandlers = new DefaultMessageHandlerRegister(controllers).getCommandExecutors();
    }

    @Override
    public void onSessionCreated(IdSession session) {
        session.setAttribute(SessionProperties.DISTRIBUTE_KEY,
                SessionManager.INSTANCE.getNextSessionId());
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

    /**
     * 将各种参数转为被RequestMapper注解的方法的实参
     *
     * @param session
     * @param methodParams
     * @param message
     * @return
     */
    private Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Object message) {
        Object[] result = new Object[methodParams == null ? 0 : methodParams.length];
        // 方法签名如果有两个参数，则为  method(IdSession session, Object message);
        //       如果有三个参数，则为  method(IdSession session, int index, Object message);
        for (int i = 0; i < result.length; i++) {
            Class<?> param = methodParams[i];
            if (i == 0) {
                if (IdSession.class.isAssignableFrom(param)) {
                    result[i] = session;
                } else {
                    throw new IllegalArgumentException("message (" + message.getClass().getSimpleName()+") handler 1st argument must be IdSession");
                }
            }
            if (result.length == 2 && i == 1) {
                if (DefaultMessageFactory.getInstance().contains(message.getClass())) {
                    result[i] = message;
                } else {
                    throw new IllegalArgumentException("message (" + message.getClass().getSimpleName()+") handler 2nd argument must be registered Message");
                }
            }
            if (result.length == 3) {
                if (i== 1) {
                    if (int.class.isAssignableFrom(param)){
                        Traceable traceable = (Traceable) message;
                        result[i] = traceable.getIndex();
                    } else{
                        throw new IllegalArgumentException("2nd argument must be int");
                    }
                }
                if (i== 2) {
                    if (DefaultMessageFactory.getInstance().contains(message.getClass())){
                        result[i] = message;
                    } else{
                        throw new IllegalArgumentException("message (" + message.getClass().getSimpleName()+") handler 3nd argument must be registered Message");
                    }
                }
            }
        }

        return result;
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
            GameExecutor.getInstance().acceptTask(closeTask);
        }
    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {
        logger.error("", cause);
    }

}