package jforgame.socket.core.client;

import jforgame.socket.core.session.IdSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple API for request callback, mainly used for cross-server communication.
 * Provides three invocation methods:
 * 1. Callback invocation {@link #callBack(IdSession, Object, RequestCallback)}, callback will be invoked when response arrives.
 * 2. Synchronous invocation {@link #request(IdSession, Object)}, request blocks until response arrives or timeout, not recommended for high-frequency requests!
 * 3. Future invocation {@link #future(IdSession, Object)}, returns CompletableFuture. Especially suitable for nested requests.
 */
public class RpcMessageClient {

    private static final AtomicInteger idFactory = new AtomicInteger(100);

    private static final int CALL_BACK_TIME_OUT = 5000;

    /**
     * Sends a message to the specified session and registers a callback.
     * When response arrives, the callback will be invoked.
     * If no response arrives, a CallbackTimeoutException will be thrown.
     *
     * @param session  target session
     * @param request  request message
     * @param callBack response callback
     * @throws CallbackTimeoutException when callback is timeout, an exception will be thrown
     */
    public static void callBack(IdSession session, Object request, RequestCallback callBack) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        final RequestResponseFuture requestResponseFuture = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, callBack);
        CallBackService.getInstance().register(index, requestResponseFuture);
        session.send(index, request);
    }

    /**
     * Sends a message to the specified session and blocks waiting for response.
     * If no response arrives, a CallbackTimeoutException will be thrown.
     *
     * @param session socket session target session
     * @param request traceable message request message
     * @return response message response message
     * @throws CallbackTimeoutException when callback is timeout, an exception will be thrown
     */
    public static Object request(IdSession session, Object request) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        RequestResponseFuture future = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, null);
        CallBackService.getInstance().register(index, future);

        try {
            session.send(index, request);
            // Block waiting (up to timeout)
            future.waitResponseMessage(CALL_BACK_TIME_OUT);
            // 1. First check if already timed out (timer task has set exception)
            if (future.getCause() != null) {
                if (future.getCause() instanceof CallbackTimeoutException) {
                    throw (CallbackTimeoutException) future.getCause();
                }
                throw new RuntimeException(future.getCause());
            }

            // 2. If still no response after waiting ends -> means it really timed out
            if (future.getResponseMsg() == null) {
                throw new CallbackTimeoutException("sync request timeout " + CALL_BACK_TIME_OUT + "ms");
            }

            return future.getResponseMsg();
        } finally {
            // Cleanup, timer scan will also handle as fallback
            CallBackService.getInstance().remove(index);
        }
    }

    /**
     * Sends a message to the specified session and returns a CompletableFuture.
     * When response arrives, the CompletableFuture will be completed.
     * If no response arrives, the CompletableFuture will be completed exceptionally.
     *
     * @param session socket session
     * @param request traceable message
     * @return response message
     */
    public static CompletableFuture<Object> future(IdSession session, Object request) {
        int index = idFactory.getAndIncrement();
        CompletableFuture<Object> completable = new CompletableFuture<>();
        RequestCallback<Object> callback = new RequestCallback<Object>() {
            @Override
            public void onSuccess(Object callBack) {
                completable.complete(callBack);
            }

            @Override
            public void onError(Throwable error) {
                completable.completeExceptionally(error);
            }
        };
        final RequestResponseFuture future = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, callback);
        CallBackService.getInstance().register(index, future);
        session.send(index, request);
        return completable;
    }

}
