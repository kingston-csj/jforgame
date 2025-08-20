package jforgame.socket.client;

import jforgame.commons.thread.NamedThreadFactory;

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

/**
 * 处理客户端请求回调服务
 * 包括同步和异步两种方式
 */
public class CallBackService {

    private static volatile CallBackService self;

    private ScheduledFuture<?> timer;

    private final ConcurrentMap<Integer, RequestResponseFuture> mapper = new ConcurrentHashMap<>();

    private ScheduledExecutorService service;

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
                self = newObj;
                self.service = Executors.newScheduledThreadPool(2, new NamedThreadFactory("socket-client-timer"));

                self.timer = self.service.scheduleAtFixedRate(
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

    public void closeTimer() {
        self.timer.cancel(true);
    }

    /**
     * 定时异常过期的回调
     */
    public void scanExpiredRequest() {
        List<RequestResponseFuture> rfList = new LinkedList<>();
        Iterator<Map.Entry<Integer, RequestResponseFuture>> it = mapper.entrySet().iterator();

        RequestResponseFuture rf;
        while (it.hasNext()) {
            Map.Entry<Integer, RequestResponseFuture> next = it.next();
            rf = next.getValue();
            if (rf.isTimeout()) {
                it.remove();
                rfList.add(rf);
            }
        }

        for (RequestResponseFuture requestResponseFuture : rfList) {
            rf = requestResponseFuture;
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
        RequestCallback callback = future.getRequestCallback();
        if (callback != null) {
            // 异步回调
            callback.onSuccess(message.getResponse());
            return;
        }
        // 同步回调
        String errorText = message.getErrorText();
        if (errorText != null && !errorText.isEmpty()) {
            Throwable t = new RuntimeException(errorText);
            future.setCause(t);
        }
        future.putResponseMessage(message.getResponse());
    }

}