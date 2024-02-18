package jforgame.socket.client;

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

    public RequestResponseFuture waitResponseMessage(long timeout) throws InterruptedException {
        this.countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public void putResponseMessage(Object responseMsg) {
        this.responseMsg = responseMsg;
        this.countDownLatch.countDown();
    }

    public long getCorrelationId() {
        return this.correlationId;
    }

    public long getBeginTimestamp() {
        return this.beginTimestamp;
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

    public RequestCallback getRequestCallback() {
        return requestCallback;
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }
}