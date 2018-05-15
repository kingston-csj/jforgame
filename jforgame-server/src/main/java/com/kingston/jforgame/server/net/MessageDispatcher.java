package com.kingston.jforgame.server.net;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.common.utils.ClassScanner;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.annotation.RequestMapping;
import com.kingston.jforgame.socket.message.CmdExecutor;
import com.kingston.jforgame.socket.message.IMessageDispatcher;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.SessionManager;
import com.kingston.jforgame.socket.session.SessionProperties;
import com.kingston.jforgame.socket.task.MessageTask;
import com.kingston.jforgame.socket.task.TaskHandlerContext;

public class MessageDispatcher implements IMessageDispatcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

    /** [module_cmd, CmdExecutor] */
    private static final Map<String, CmdExecutor> MODULE_CMD_HANDLERS = new HashMap<>();

    public MessageDispatcher() {
        initialize();
    }

    private void initialize() {
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation("com.kingston.jforgame.server.game", Controller.class);

        for (Class<?> controller: controllers) {
            try {
                Object handler = controller.newInstance();
                Method[] methods = controller.getDeclaredMethods();
                for (Method method:methods) {
                    RequestMapping mapperAnnotation = method.getAnnotation(RequestMapping.class);
                    if (mapperAnnotation != null) {
                        short[] meta = getMessageMeta(method);
                        if (meta == null) {
                            throw new RuntimeException(String.format("controller[%s] method[%s] lack of RequestMapping annotation",
                                    controller.getName(), method.getName()));
                        }
                        short module = meta[0];
                        short cmd    = meta[1];
                        String key = buildKey(module, cmd);
                        CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(key);
                        if (cmdExecutor != null) {
                            throw new RuntimeException(String.format("module[%d] cmd[%d] duplicated", module, cmd));
                        }

                        cmdExecutor = CmdExecutor.valueOf(method, method.getParameterTypes(), handler);
                        MODULE_CMD_HANDLERS.put(key, cmdExecutor);
                    }
                }
            }catch(Exception e) {
            	logger.error("", e);
            }
        }
    }

    /**
     * 返回方法所带Message参数的元信息
     * @param method
     * @return
     */
    private short[] getMessageMeta(Method method) {
        for (Class<?> paramClazz: method.getParameterTypes()) {
            if (Message.class.isAssignableFrom(paramClazz)) {
                MessageMeta protocol = paramClazz.getAnnotation(MessageMeta.class);
                if (protocol != null) {
                	short[] meta = {protocol.module(), protocol.cmd()};
                	return meta;
                }
            }
        }
        return null;
    }

    @Override
    public void dispatch(IoSession session, Message message) {
        short module = message.getModule();
        short cmd    = message.getCmd();

        CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(buildKey(module, cmd));
        if (cmdExecutor == null) {
        	logger.error("message executor missed, module={},cmd={}", module, cmd);
            return;
        }

        Object[] params = convertToMethodParams(session, cmdExecutor.getParams(), message);
        Object controller = cmdExecutor.getHandler();

        int distributeKey = (int)session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
        TaskHandlerContext.INSTANCE.acceptTask(
        		MessageTask.valueOf(distributeKey, controller, cmdExecutor.getMethod(), params));

//        不在io线程进行业务处理
//        try {
//            //通过反射，
//            cmdExecutor.getMethod().invoke(controller, params);
//        }catch(Exception e) {
//        }
    }

    /**
     * 将各种参数转为被RequestMapper注解的方法的实参
     * @param session
     * @param methodParams
     * @param message
     * @return
     */
    private Object[] convertToMethodParams(IoSession session, Class<?>[] methodParams, Message message) {
        Object[] result = new Object[methodParams==null?0:methodParams.length];

        for (int i=0;i<result.length;i++) {
            Class<?> param = methodParams[i];
            if (IoSession.class.isAssignableFrom(param)) {
                result[i] = session;
            }else if (Long.class.isAssignableFrom(param)) {
                result[i] = SessionManager.INSTANCE.getPlayerIdBy(session);
            }else if (long.class.isAssignableFrom(param)) {
                result[i] = SessionManager.INSTANCE.getPlayerIdBy(session);
            }else if (Message.class.isAssignableFrom(param)) {
                result[i] = message;
            }
        }

        return result;
    }

    private String buildKey(short module, short cmd) {
        return module + "_" + cmd;
    }

}