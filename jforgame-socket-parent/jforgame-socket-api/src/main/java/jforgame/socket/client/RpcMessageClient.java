package jforgame.socket.client;

import jforgame.socket.share.IdSession;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcMessageClient {

    private static final AtomicInteger idFactory = new AtomicInteger(100);

    private static int CALL_BACK_TIME_OUT = 5000;

    /**
     * send a message to an appointed session and then register a callback
     * when response arrives, the callback will be invoked.
     * If no response arrives, a CallbackTimeoutException exception will be thrown.
     * @param session target session
     * @param request request message
     * @param callBack response callback
     * @throws CallbackTimeoutException
     */
    public static void callBack(IdSession session, Traceable request, RequestCallback callBack) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        request.setIndex(index);

        final RequestResponseFuture requestResponseFuture = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, callBack);
        CallBackService.getInstance().register(index, requestResponseFuture);
        session.send(index, request);
    }

    /**
     * send a message to an appointed session, a response message will be returned as a return value.
     * If no response arrives, a CallbackTimeoutException exception will be thrown.
     * @param session
     * @param request
     * @return
     * @throws CallbackTimeoutException
     */
    public static Object request(IdSession session, Traceable request) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        request.setIndex(index);
        session.send(index, request);
        final RequestResponseFuture future = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, null);

        CallBackService.getInstance().register(index, future);
        try {
            RequestResponseFuture responseMessage = future.waitResponseMessage(CALL_BACK_TIME_OUT);
            if (responseMessage == null) {
                CallbackTimeoutException exception = new CallbackTimeoutException("send request message  failed");
                future.setCause(exception);

                throw exception;
            }
            return responseMessage.getResponseMsg();
        } catch (InterruptedException e) {
            future.setCause(e);
            CallBackService.getInstance().remove(index);
        }
        return null;
    }

}