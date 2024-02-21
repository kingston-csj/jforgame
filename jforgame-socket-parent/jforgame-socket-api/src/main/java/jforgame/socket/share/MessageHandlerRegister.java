package jforgame.socket.share;

import jforgame.socket.share.message.MessageExecutor;

import java.util.Map;

public interface MessageHandlerRegister {

    Map<Integer, MessageExecutor> getCommandExecutors();
}
