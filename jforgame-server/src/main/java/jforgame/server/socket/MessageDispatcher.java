package jforgame.server.socket;

import jforgame.commons.ClassScanner;
import jforgame.server.game.GameContext;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestMapping;
import jforgame.socket.share.message.CmdExecutor;
import jforgame.socket.share.message.IMessageDispatcher;
import jforgame.socket.share.message.Message;
import jforgame.socket.share.task.BaseGameTask;
import jforgame.socket.share.task.MessageTask;
import jforgame.socket.support.DefaultMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageDispatcher implements IMessageDispatcher {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * [module_cmd, CmdExecutor]
     */
    private static final Map<Integer, CmdExecutor> MODULE_CMD_HANDLERS = new HashMap<>();

    public MessageDispatcher(String scanPath) {
        initialize(scanPath);
    }

    private void initialize(String scanPath) {
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation(scanPath,
                MessageRoute.class);

        for (Class<?> controller : controllers) {
            try {
                Object handler = controller.newInstance();
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    RequestMapping mapperAnnotation = method.getAnnotation(RequestMapping.class);
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
                        CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(key);
                        if (cmdExecutor != null) {
                            throw new RuntimeException(String.format("module[%d] cmd[%d] duplicated", module, cmd));
                        }

                        cmdExecutor = CmdExecutor.valueOf(method, method.getParameterTypes(), handler);
                        MODULE_CMD_HANDLERS.put(key, cmdExecutor);
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

    @Override
    public void onSessionCreated(IdSession session) {
        session.setAttribute(SessionProperties.DISTRIBUTE_KEY,
                SessionManager.INSTANCE.getNextSessionId());
    }

    @Override
    public void dispatch(IdSession session, Object message) {
        int cmd = DefaultMessageFactory.getInstance().getMessageId(message.getClass());
        CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(cmd);
        if (cmdExecutor == null) {
            logger.error("message executor missed,  cmd={}", cmd);
            return;
        }

        Object[] params = convertToMethodParams(session, cmdExecutor.getParams(), message);
        Object controller = cmdExecutor.getHandler();

        int sessionId = (int) session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
        MessageTask task = MessageTask.valueOf(session, sessionId, controller, cmdExecutor.getMethod(), params);
        task.setMessage(message);
        // 丢到任务消息队列，不在io线程进行业务处理
        GameExecutor.getInstance().acceptTask(task);
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

        for (int i = 0; i < result.length; i++) {
            Class<?> param = methodParams[i];
            if (IdSession.class.isAssignableFrom(param)) {
                result[i] = session;
            } else if (Long.class.isAssignableFrom(param)) {
                result[i] = session.getOwnerId();
            } else if (long.class.isAssignableFrom(param)) {
                result[i] = session.getOwnerId();
            } else if (Message.class.isAssignableFrom(param)) {
                result[i] = message;
            } else if(DefaultMessageFactory.getInstance().contains(message.getClass())){
                result[i] = message;
            }
        }

        return result;
    }

    private int buildKey(int module, int cmd) {
        int result = Math.abs(module) * 1000 + Math.abs(cmd);
        return cmd < 0 ? -result : result;
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