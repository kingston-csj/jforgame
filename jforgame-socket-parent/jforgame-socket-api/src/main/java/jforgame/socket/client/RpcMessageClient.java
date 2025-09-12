package jforgame.socket.client;

import jforgame.socket.share.IdSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为请求回调提供简易的API，主要用于跨服通信
 */
public class RpcMessageClient {

    private static final AtomicInteger idFactory = new AtomicInteger(100);

    private static final int CALL_BACK_TIME_OUT = 5000;

    /**
     * 往指定会话发送一个消息，并注册一个回调，当响应 arrives, 回调将被调用。
     * 如果没有响应 arrives, 一个 CallbackTimeoutException 异常将被抛出。
     *
     * @param session  target session 目标会话
     * @param request  request message 请求消息
     * @param callBack response callback 响应回调
     * @throws CallbackTimeoutException when callback is timeout, an exception will be thrown
     */
    public static void callBack(IdSession session, Object request, RequestCallback callBack) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        final RequestResponseFuture requestResponseFuture = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, callBack);
        CallBackService.getInstance().register(index, requestResponseFuture);
        session.send(index, request);
    }

    /**
     * 往指定会话发送一个消息，并阻塞等待响应消息返回。
     * 如果没有响应 arrives, 一个 CallbackTimeoutException 异常将被抛出。
     *
     * @param session socket session 目标会话
     * @param request traceable message 请求消息
     * @return response message 响应消息
     * @throws CallbackTimeoutException when callback is timeout, an exception will be thrown 当回调超时，将抛出异常
     */
    public static Object request(IdSession session, Object request) throws CallbackTimeoutException {
        int index = idFactory.getAndIncrement();
        session.send(index, request);
        final RequestResponseFuture future = new RequestResponseFuture(index, CALL_BACK_TIME_OUT, null);

        CallBackService.getInstance().register(index, future);
        try {
            RequestResponseFuture responseMessage = future.waitResponseMessage(CALL_BACK_TIME_OUT);
            if (responseMessage.getCause() != null) {
                if (responseMessage.getCause() instanceof CallbackTimeoutException) {
                    throw (CallbackTimeoutException) responseMessage.getCause();
                }
                throw new RuntimeException(responseMessage.getCause());
            }
            return responseMessage.getResponseMsg();
        } catch (InterruptedException e) {
            future.setCause(e);
            CallBackService.getInstance().remove(index);
        }
        return null;
    }

    /**
     * 往指定会话发送一个消息，并返回一个 CompletableFuture 对象，当响应 arrives,  CompletableFuture 将被完成。
     * 如果没有响应 arrives,  CompletableFuture 将被 exceptionally 完成。
     *
     * @param session socket session 目标会话
     * @param request traceable message 请求消息
     * @return response message 响应消息
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
