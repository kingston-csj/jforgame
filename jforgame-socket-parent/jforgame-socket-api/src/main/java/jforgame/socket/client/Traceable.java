package jforgame.socket.client;

/**
 * a message implements {@link Traceable} is used for callback
 * 客户端序号由包头承载，该接口后续移除
 * @see RpcMessageClient
 */
@Deprecated
public interface Traceable {

    /**
     * get index of the message
     *
     * @return get index of the message
     */
    int getIndex();

    /**
     * set index of the message
     * this method will be invoked automatically
     */
    void setIndex(int index);
}
