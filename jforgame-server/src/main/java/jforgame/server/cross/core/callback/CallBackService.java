package jforgame.server.cross.core.callback;

import jforgame.common.utils.ClassScanner;
import jforgame.server.ServerScanPaths;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.thread.SchedulerManager;
import jforgame.socket.message.Message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

public class CallBackService {

    private static volatile CallBackService self;

    private ScheduledFuture<?> timer;

    private ConcurrentMap<Integer, RequestResponseFuture> mapper = new ConcurrentHashMap<>();

    public void register(int correlationId, RequestResponseFuture future) {
        mapper.put(correlationId, future);
    }

    public RequestResponseFuture remove(int correlationId) {
        return mapper.remove(correlationId);
    }

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
                        CallbackHandler handler = (CallbackHandler) clazz.newInstance();
                        CallbackHandler.register(handler);
                    } catch (Exception e) {
                        LoggerUtils.error("", e);
                    }
                }
                self = newObj;

                newObj.timer = SchedulerManager.scheduleAtFixedRate(() -> {
                    try {
                        newObj.scanExpiredRequest();
                    } catch (Exception e) {
                        LoggerUtils.error("", e);
                    }
                }, 3000, 1000);
            }
        }
        return self;
    }

    /**
     * 定时异常过期的回调
     */
    public void scanExpiredRequest() {
        List<RequestResponseFuture> rfList = new LinkedList();
        Iterator it = mapper.entrySet().iterator();

        RequestResponseFuture rf;
        while (it.hasNext()) {
            Map.Entry<String, RequestResponseFuture> next = (Map.Entry) it.next();
            rf = next.getValue();
            if (rf.isTimeout()) {
                it.remove();
                rfList.add(rf);
            }
        }

        Iterator var6 = rfList.iterator();
        while (var6.hasNext()) {
            rf = (RequestResponseFuture) var6.next();

            try {
                Throwable cause = new CallTimeoutException("request timeout, no reply");
                rf.setCause(cause);
                rf.executeRequestCallback();
            } catch (Throwable ignore) {
            }
        }
    }

    public void fillCallBack(int index, Message message) {
        RequestResponseFuture future = remove(index);
        if (future == null) {
            LoggerUtils.error("回调信息丢失 msg {}", message);
            return;
        }
        RequestCallback callback = future.getRequestCallback();
        if (callback != null) {
            callback.onSuccess(message);
        }
        if (future != null) {
            future.putResponseMessage(message);
        }
    }

}
