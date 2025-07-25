package jforgame.socket.client;

/**
 * 请求对应的回调接口
 */
public interface RequestCallback<T> {

    /**
     * 请求方接受回调消息的业务处理
     *
     * @param callBack 回调的响应消息
     */
    void onSuccess(T callBack);

    void onError(Throwable error);

}