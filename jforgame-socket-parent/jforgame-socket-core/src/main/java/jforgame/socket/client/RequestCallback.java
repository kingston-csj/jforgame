package jforgame.socket.client;

/**
 * 请求对应的回调接口
 */
public interface RequestCallback<T> {

    /**
     * 回调成功的处理
     *
     * @param callBack 回调的响应消息
     */
    void onSuccess(T callBack);

    /**
     * 回调失败的处理
     *
     * @param error 错误信息
     */
    void onError(Throwable error);

}