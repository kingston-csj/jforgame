package jforgame.server.cross.core.callback;

import jforgame.server.cross.core.client.C2SSessionPoolFactory;
import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.message.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CallbackTask implements Callable<Message> {

    private CCSession session;

    private Message request;

    public static CallbackTask valueOf(CCSession session, Message message) {
        CallbackTask task = new CallbackTask();
        task.request = message;
        task.session = session;
        return task;
    }

    @Override
    public Message call() throws Exception {
        try {
            G2FCallBack reqCallBack = (G2FCallBack) request;
            int index = RpcResponse.nextMsgId();
            RpcResponse callBack = new RpcResponse();
            callBack.setIndex(index);
            CountDownLatch latch = new CountDownLatch(1);
            callBack.setLatch(latch);
            reqCallBack.setIndex(index);
            reqCallBack.serialize();

            session.sendMessage(reqCallBack);

            CallBackService.getInstance().registerCallback(index, callBack);
            latch.await(5, TimeUnit.SECONDS);
            if (callBack.getData() != null) {
                return callBack.getData();
            }
        } finally {
            C2SSessionPoolFactory.getInstance().returnSession(session);
        }
        // 超时返回
        throw new CallTimeoutException("跨服回调超时 " + session);
    }
}