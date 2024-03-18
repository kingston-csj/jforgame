package jforgame.socket.client;

/**
 * a message implements {@link Traceable} is used for callback
 * @see RpcMessageClient
 */
public interface Traceable {

    /**
     * get index of the message
     *
     * @return
     */
    int getIndex();

    /**
     * set index of the message
     * this method will be invoked automaticly
     *
     * @return
     */
    void setIndex(int index);
}
