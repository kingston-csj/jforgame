package jforgame.socket.client;

import jforgame.socket.IdSession;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcBlockClient implements RpcCallback {

    private AtomicInteger idFactory = new AtomicInteger();

    @Override
    public Object request(IdSession session, Traceful request) throws CallbackTimeoutException {

        int timeout = 5000;

        int index = idFactory.getAndIncrement();
        request.setIndex(index);
        session.sendPacket(request);

        final RequestResponseFuture future = new RequestResponseFuture(index, timeout, null);

        CallBackService.getInstance().register(index, future);
        try {
            RequestResponseFuture responseMessage = future.waitResponseMessage(timeout);
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
