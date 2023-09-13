package jforgame.socket.client;

import jforgame.socket.IdSession;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcCallbackClient {

    private AtomicInteger idFactory = new AtomicInteger();

    public void callBack(IdSession session, Traceful request, RequestCallback callBack) throws CallbackTimeoutException {

        int timeout = 5000;

        int index = idFactory.getAndIncrement();
        request.setIndex(index);
        session.sendPacket(request);

        final RequestResponseFuture requestResponseFuture = new RequestResponseFuture(index, timeout, callBack);
        CallBackService.getInstance().register(index, requestResponseFuture);
        session.sendPacket(request);
    }
}
