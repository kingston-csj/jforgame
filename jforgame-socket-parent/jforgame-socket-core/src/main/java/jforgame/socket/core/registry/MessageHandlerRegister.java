package jforgame.socket.core.registry;

/**
 * Message handler register, for registering message handling methods
 */
public interface MessageHandlerRegister {
    /**
     * Register message handling method
     *
     * @param cmd      message cmd
     * @param executor message handling method
     */
    void register(int cmd, MessageExecutor executor);

    /**
     * Get message handling method
     *
     * @param cmd message cmd
     * @return message handling method
     */
    MessageExecutor getMessageExecutor(int cmd);

}
