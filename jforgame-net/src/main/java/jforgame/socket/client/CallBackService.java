package jforgame.socket.client;

import jforgame.common.thread.NamedThreadFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CallBackService {

    private static volatile CallBackService self;

    private ScheduledFuture<?> timer;

    private ConcurrentMap<Integer, RequestResponseFuture> mapper = new ConcurrentHashMap<>();

    private ScheduledExecutorService service;

    public void register(int correlationId, RequestResponseFuture future) {
        mapper.put(correlationId, future);
    }

    public RequestResponseFuture remove(long correlationId) {
        return mapper.remove(correlationId);
    }

    public static CallBackService getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (CallBackService.class) {
            if (self == null) {
                CallBackService newObj = new CallBackService();
                self = newObj;
                self.service = Executors.newScheduledThreadPool(2, new NamedThreadFactory("common-scheduler"));

                newObj.timer = self.service.scheduleAtFixedRate(
                        () -> {
                            try {
                                newObj.scanExpiredRequest();
                            } catch (Exception e) {
                            }
                        }, 3000, 1000, TimeUnit.MILLISECONDS);
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
                Throwable cause = new CallbackTimeoutException("request timeout, no reply");
                rf.setCause(cause);
                rf.executeRequestCallback();
            } catch (Throwable ignore) {
            }
        }
    }

    public void fillCallBack(int index, RpcResponseData message) {
        RequestResponseFuture future = remove(index);
        if (future == null) {
            return;
        }
        if (future != null) {
            String errorText = message.getErrorText();
            if (errorText != null && errorText.length() > 0) {
                Throwable t = new RuntimeException(errorText);
                future.setCause(t);
            }
            future.putResponseMessage(message.getResponse());
        }
    }

}