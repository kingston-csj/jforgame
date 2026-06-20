package jforgame.socket.core.client;

/**
 * Callback interface for request responses
 */
public interface RequestCallback<T> {

    /**
     * Callback for successful response
     *
     * @param callBack response message
     */
    void onSuccess(T callBack);

    /**
     * Callback for failed response
     *
     * @param error error information
     */
    void onError(Throwable error);

}