package jforgame.socket.core.client;

/**
 * 回调超时异常
 */
public class CallbackTimeoutException extends RuntimeException {

    public CallbackTimeoutException(String message) {
        super(message);
    }

}
