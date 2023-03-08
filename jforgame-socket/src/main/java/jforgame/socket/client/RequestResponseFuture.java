package jforgame.socket.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RequestResponseFuture {

    private final long correlationId;
    private final long beginTimestamp = System.currentTimeMillis();
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile Object responseMsg = null;
    private volatile Throwable cause = null;
    private long timeoutMillis;

    public RequestResponseFuture(long correlationId, long timeOut) {
        this.correlationId = correlationId;
        this.timeoutMillis = timeOut;
    }

    public void executeRequestCallback() {
        this.cause = new CallbackTimeoutException(correlationId + " timeout");
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

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }
}