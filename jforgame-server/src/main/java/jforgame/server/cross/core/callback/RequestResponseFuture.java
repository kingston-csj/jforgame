package jforgame.server.cross.core.callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RequestResponseFuture {

    private final int correlationId;
    private final RequestCallback requestCallback;
    private final long beginTimestamp = System.currentTimeMillis();
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile Object responseMsg = null;
    private volatile Throwable cause = null;

    private long timeoutMillis;

    public RequestResponseFuture(int correlationId, long timeOut, RequestCallback requestCallback) {
        this.correlationId = correlationId;
        this.requestCallback = requestCallback;
        this.timeoutMillis = timeOut;
    }

    public void executeRequestCallback() {
        if (this.requestCallback != null) {
            if (this.cause == null) {
                this.requestCallback.onSuccess(this.responseMsg);
            } else {
                this.requestCallback.onError(this.cause);
            }
        }
    }

    public Object waitResponseMessage(long timeout) throws InterruptedException {
        this.countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        return this.responseMsg;
    }

    public void putResponseMessage(Object responseMsg) {
        this.responseMsg = responseMsg;
        this.countDownLatch.countDown();
    }

    public int getCorrelationId() {
        return this.correlationId;
    }

    public RequestCallback getRequestCallback() {
        return this.requestCallback;
    }

    public long getBeginTimestamp() {
        return this.beginTimestamp;
    }

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public Object getResponseMsg() {
        return this.responseMsg;
    }

    public void setResponseMsg(Object responseMsg) {
        this.responseMsg = responseMsg;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }
}
