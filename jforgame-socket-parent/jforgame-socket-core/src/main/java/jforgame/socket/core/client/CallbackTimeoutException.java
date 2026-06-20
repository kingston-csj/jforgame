package jforgame.socket.core.client;

/**
 * Callback timeout exception
 */
public class CallbackTimeoutException extends RuntimeException {

    public CallbackTimeoutException(String message) {
        super(message);
    }

}
