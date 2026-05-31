package jforgame.socket.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的消息处理注册器
 */
public class DefaultMessageHandlerRegister implements MessageHandlerRegister {

    /**
     * [module_cmd, CmdExecutor]
     */
    private Map<Integer, MessageExecutor> cmdHandlers = new ConcurrentHashMap<>();

    @Override
    public void register(int cmd, MessageExecutor executor) {
        cmdHandlers.put(cmd, executor);
    }

    @Override
    public MessageExecutor getMessageExecutor(int cmd) {
        return cmdHandlers.get(cmd);
    }


}
