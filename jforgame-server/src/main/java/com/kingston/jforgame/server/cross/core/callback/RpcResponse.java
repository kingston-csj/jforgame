package com.kingston.jforgame.server.cross.core.callback;


import com.kingston.jforgame.server.logs.LoggerUtils;
import com.kingston.jforgame.socket.message.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcResponse {

    private int index;

    private Message data;

    private final static AtomicInteger idFactory = new AtomicInteger();

    public static int nextMsgId() {
        return idFactory.getAndIncrement();
    }

    protected void doTimeOut() {
        LoggerUtils.error("回调方法已过期");
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
}