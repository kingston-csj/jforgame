package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.socket.message.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CallBackService {

    private static CallBackService self = new CallBackService();

    private ConcurrentMap<Integer, CallBack> callbacks = new ConcurrentHashMap<>();

    public static CallBackService getInstance() {
        return self;
    }

    public void fillCallBack(int index, Message message) {
        CallBack callBack = new CallBack();
        callBack.setIndex(index);
        callBack.setData(message);

        callbacks.put(index, callBack);
    }

    public CallBack removeCallBack(int index) {
        return callbacks.remove(index);
    }


}
