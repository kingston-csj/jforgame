package jforgame.demo.socket;

import jforgame.commons.ClassScanner;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.socket.share.ChainedMessageDispatcher;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.MessageParameterConverter;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;
import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.task.BaseGameTask;
import jforgame.socket.share.task.MessageTask;
import jforgame.socket.support.DefaultMessageHandlerRegister;
import jforgame.socket.support.DefaultMessageParameterConverter;
import jforgame.socket.support.MessageExecuteUnit;

import java.lang.reflect.Method;
import java.util.Set;

public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    private MessageParameterConverter msgParameterConverter = new DefaultMessageParameterConverter(GameMessageFactory.getInstance());

    public MessageIoDispatcher(String scanPath) {
        initialize(scanPath);

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
            GameExecutor.getInstance().acceptTask(task);
            return true;
        };

        addMessageHandler(messageHandler);
    }

    private void initialize(String scanPath) {
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation(scanPath,
                MessageRoute.class);
        this.handlerRegister = new DefaultMessageHandlerRegister();
        for (Class<?> controller : controllers) {
            try {
                Object handler = controller.newInstance();
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    RequestHandler mapperAnnotation = method.getAnnotation(RequestHandler.class);
                    if (mapperAnnotation != null) {
                        int[] meta = getMessageMeta(method);
                        if (meta == null) {
                            throw new RuntimeException(
                                    String.format("controller[%s] method[%s] lack of RequestMapping annotation",
                                            controller.getName(), method.getName()));
                        }
                        int module = meta[0];
                        int cmd = meta[1];
                        int key = buildKey(module, cmd);
                        MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(key);
                        if (cmdExecutor != null) {
                            throw new RuntimeException(String.format("module[%d] cmd[%d] duplicated", module, cmd));
                        }

                        cmdExecutor = MessageExecuteUnit.valueOf(method, method.getParameterTypes(), handler);
                        handlerRegister.register(key, cmdExecutor);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 返回方法所带Message参数的元信息
     *
     * @param method
     * @return
     */
    private int[] getMessageMeta(Method method) {
        for (Class<?> paramClazz : method.getParameterTypes()) {
            MessageMeta protocol = paramClazz.getAnnotation(MessageMeta.class);
            if (protocol != null) {
                int[] meta = {protocol.module(), protocol.cmd()};
                return meta;
            }
        }
        return null;
    }

    private int buildKey(int module, int cmd) {
        int result = Math.abs(module) * 1000 + Math.abs(cmd);
        return cmd < 0 ? -result : result;
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
            GameExecutor.getInstance().acceptTask(closeTask);
        }
    }

}