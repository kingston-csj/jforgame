package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.common.utils.TimeUtil;
import com.kingston.jforgame.server.cross.core.client.C2SSessionPoolFactory;
import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.socket.message.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
            CReqCallBack reqCallBack = (CReqCallBack) request;
            int index = RpcResponse.nextMsgId();
            reqCallBack.setIndex(index);
            reqCallBack.serialize();

            session.sendMessage(reqCallBack);

            ReentrantLock lock = new ReentrantLock();
            Condition condition = lock.newCondition();
            long maxTimeOut = 5 * TimeUtil.ONE_SECOND;
            long startTime = System.currentTimeMillis();
            CallBackService callBackService = CallBackService.getInstance();
            while (System.currentTimeMillis() - startTime <= maxTimeOut) {
                RpcResponse c = callBackService.removeRpcResponse(index);
                if (c != null) {
                    return c.getData();
                }

                try {
                    lock.lockInterruptibly();
                    condition.await(10, TimeUnit.MILLISECONDS);
                } catch (Exception ignore) {

                } finally {
                    lock.unlock();
                }
            }
        } finally {
            C2SSessionPoolFactory.getInstance().returnSession(session);
        }
        // 超时返回
        throw new CallTimeoutException("跨服回调超时 " + session);
    }
}