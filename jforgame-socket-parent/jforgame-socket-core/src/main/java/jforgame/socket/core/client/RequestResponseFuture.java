package jforgame.socket.core.client;

import jforgame.socket.core.session.IdSession;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Client request response Future, used to handle client response data.
 * If client sends request synchronously via {@link RpcMessageClient#request(IdSession, Object)},
 * the response will be returned via {@link #waitResponseMessage(long)};
 * If client sends request asynchronously via {@link RpcMessageClient#callBack(IdSession, Object, RequestCallback)},
 * the response will be returned via {@link #putResponseMessage(Object)};
 */
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

    public RequestResponseFuture waitResponseMessage(long timeout) {
        try {
            boolean completed = this.countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
            // [Optional] Set exception in advance, CallBackService#scanExpiredRequest will also handle it as fallback
            if (!completed && this.cause == null) {
                this.cause = new CallbackTimeoutException("request timeout, no reply");
            }
        } catch (InterruptedException e) {
            // Restore interrupt status (standard concurrent programming practice)
            Thread.currentThread().interrupt();
            this.cause = e;
            // Wake up waiting
            this.countDownLatch.countDown();
        }
        return this;
    }

    public void putResponseMessage(Object responseMsg) {
        this.responseMsg = responseMsg;
        this.countDownLatch.countDown();
    }

    public int getCorrelationId() {
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