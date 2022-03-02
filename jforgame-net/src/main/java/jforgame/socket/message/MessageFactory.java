package jforgame.socket.message;

public interface MessageFactory {


    /**
     * binding  messageId and messageClass
     * if messageId repeated, an IllegalStateException exception thrown
     * @param cmd
     * @param clazz
     */
    void registerMessage(int cmd, Class<?> clazz);

    /**
     * return message class of the appointed messageId
     * @param cmd
     * @return
     */
     Class<?> getMessage(int cmd);

    /**
     * return id of the message
     * @param clazz
     * @return
     */
     int getMessageId(Class<?> clazz);
}
