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

    /**
     * [module_cmd, CmdExecutor]
     */
    private  Map<Integer, MessageExecutor> cmdHandlers = new HashMap<>();

    @Override
    public void register(int cmd, MessageExecutor executor) {
        cmdHandlers.put(cmd, executor);
    }

    @Override
    public MessageExecutor getMessageExecutor(int cmd) {
        return cmdHandlers.get(cmd);
    }


}
