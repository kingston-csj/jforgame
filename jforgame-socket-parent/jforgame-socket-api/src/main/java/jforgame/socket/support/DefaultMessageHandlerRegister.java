package jforgame.socket.support;

import jforgame.socket.share.MessageHandlerRegister;
import jforgame.socket.share.message.MessageExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的消息处理注册器
 */
public class DefaultMessageHandlerRegister implements MessageHandlerRegister {

    /**
     * [module_cmd, CmdExecutor]
     */
    private Map<Integer, MessageExecutor> cmdHandlers = new HashMap<>();

    @Override
    public void register(int cmd, MessageExecutor executor) {
        cmdHandlers.put(cmd, executor);
    }

    @Override
    public MessageExecutor getMessageExecutor(int cmd) {
        return cmdHandlers.get(cmd);
    }


}
