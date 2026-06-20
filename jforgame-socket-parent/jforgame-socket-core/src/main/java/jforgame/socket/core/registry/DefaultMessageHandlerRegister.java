package jforgame.socket.core.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default message handler register
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
