package jforgame.codec.struct;

/**
 * Lightweight message factory.
 * This interface serves as a replacement for jforgame.socket.share.message#MessageFactory,
 * the purpose is to avoid introducing heavy dependencies like jforgame-socket-api.
 */
public interface LiteMessageFactory {

    /**
     * Get message class by message id
     *
     * @param cmd message id
     * @return message class
     */
    Class<?> getMessage(int cmd);

    /**
     * Get message id by message class
     *
     * @param clazz message class
     * @return message id
     */
    int getMessageId(Class<?> clazz);

    /**
     * Check if the specified message class is contained
     *
     * @param clazz message class
     * @return whether contained
     */
    boolean contains(Class<?> clazz);
}
