package jforgame.socket.support;

import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.annotation.RequestHandler;
import jforgame.socket.share.annotation.MessageMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultMessageHandlerRegister implements MessageHandlerRegister {

    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * [module_cmd, CmdExecutor]
     */
    private  Map<Integer, MessageExecutor> cmdHandlers = new HashMap<>();

    public DefaultMessageHandlerRegister(Collection<Class<?>> controllers) {
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
                        MessageExecutor cmdExecutor = cmdHandlers.get(key);
                        if (cmdExecutor != null) {
                            throw new RuntimeException(String.format("module[%d] cmd[%d] duplicated", module, cmd));
                        }

                        cmdExecutor = MessageExecuteUnit.valueOf(method, method.getParameterTypes(), handler);
                        cmdHandlers.put(key, cmdExecutor);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public Map<Integer, MessageExecutor> getCommandExecutors() {
        return cmdHandlers;
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
}
