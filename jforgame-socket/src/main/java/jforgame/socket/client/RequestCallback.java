package jforgame.socket.client;

public interface RequestCallback<T> {

    /**
     * 请求方接受回调消息的业务处理
     *
     * @param callBack
     */
    void onSuccess(T callBack);

    void onError(Throwable error);

}