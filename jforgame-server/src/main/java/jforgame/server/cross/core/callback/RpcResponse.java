package jforgame.server.cross.core.callback;


import jforgame.socket.message.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcResponse {

    private int index;

    private Message data;

    private CountDownLatch latch;

    private final static AtomicInteger idFactory = new AtomicInteger();

    public static int nextMsgId() {
        return idFactory.getAndIncrement();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Message getData() {
        return data;
    }

    public void setData(Message data) {
        this.data = data;
    }

    public boolean isDone() {
        return data != null;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}