package jforgame.server.cross.core.callback;

import jforgame.server.ServerScanPaths;
import jforgame.common.utils.ClassScanner;
import jforgame.server.logs.LoggerUtils;
import jforgame.socket.message.Message;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CallBackService {

    private static volatile CallBackService self;

    private ConcurrentMap<Integer, RpcResponse> rpcResponse = new ConcurrentHashMap<>();

    private ConcurrentMap<Integer, CallbackAction> callbacks = new ConcurrentHashMap<>();

    public static CallBackService getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (CallBackService.class) {
            if (self == null) {
                CallBackService newObj = new CallBackService();
                Set<Class<?>> clazzs = ClassScanner.listAllSubclasses(ServerScanPaths.RPC_CALL_BACK_PATH, CallbackHandler.class);
                for (Class<?> clazz : clazzs) {
                    try {
                        CallbackHandler handler = (CallbackHandler)clazz.newInstance();
                        CallbackHandler.register(handler);
                    } catch (Exception e) {
                        LoggerUtils.error("", e);
                    }
                }
                self = newObj;
            }
        }
        return self;
    }

    public void fillCallBack(int index, int rpc, Message message) {
        if (rpc == CallbackKinds.RPC_SYNC) {
            RpcResponse callBack = rpcResponse.remove(index);
            if (callBack != null) {
                callBack.setData(message);
                callBack.getLatch().countDown();
            }
        } else {
            CallbackAction callback = callbacks.remove(index);
            if (callback != null) {
                if (!callback.getFuture().isDone()) {
                    callback.getFuture().cancel(false);
                }
                callback.onMessageReceive(message);
            }
        }
    }

    public void registerCallback(int index, RpcResponse callback) {
        rpcResponse.put(index, callback);
    }

    public void registerCallback(int index, CallbackAction callback) {
        callbacks.put(index, callback);
    }

    public CallbackAction removeCallback(int index) {
        return callbacks.remove(index);
    }
}
