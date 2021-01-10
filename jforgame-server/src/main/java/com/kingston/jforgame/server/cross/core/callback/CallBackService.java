package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.socket.message.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CallBackService {

    private static CallBackService self = new CallBackService();

    private ConcurrentMap<Integer, RpcResponse> rpcResponse = new ConcurrentHashMap<>();

    private ConcurrentMap<Integer, CallbackAction> callbacks = new ConcurrentHashMap<>();

    public static CallBackService getInstance() {
        return self;
    }

    public void fillCallBack(int index, int rpc, Message message) {
        if (rpc == 0) {
            RpcResponse callBack = new RpcResponse();
            callBack.setIndex(index);
            callBack.setData(message);
            rpcResponse.put(index, callBack);
        } else {
            CallbackAction callback = callbacks.remove(index);
            if (callback != null) {
                callback.onMessageReceive(message);
            }
        }
    }

    public void registerCallback(int index, CallbackAction callback) {
        callbacks.put(index, callback);
    }

    public RpcResponse removeRpcResponse(int index) {
        return rpcResponse.remove(index);
    }

    public CallbackAction removeCallback(int index) {
        return callbacks.remove(index);
    }
}
