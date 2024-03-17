package jforgame.socket.share;

import jforgame.socket.share.message.MessageExecutor;

public interface MessageHandlerRegister {

    void register(int cmd, MessageExecutor executor);

    MessageExecutor getMessageExecutor(int cmd);

}
