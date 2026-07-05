package jforgame.socket.core.dispatch;


import jforgame.socket.core.protocol.message.MessageHeader;
import jforgame.socket.core.registry.MessageExecutor;
import jforgame.socket.core.session.IdSession;

import java.util.HashMap;
import java.util.Map;

/**
 * For each client request, there is a context for framework execution during its lifecycle
 */
public class RequestContext {

    private IdSession session;

    /**
     * Message executor
     */
    private MessageExecutor methodExecutor;

    /**
     * Request message packet
     */
    private Object request;

    /**
     * Message header corresponding to the request
     */
    private MessageHeader header;

    /**
     * Actual parameters passed to the message executor
     */
    private Object[] params;

    /**
     * Exception encountered during processing
     */
    private Throwable error;

    /**
     * Corresponding processing result (can be null)
     */
    private Object response;

    /**
     * Extended attributes
     */
    private Map<String, Object> attributes;

    public IdSession getSession() {
        return session;
    }

    public void setSession(IdSession session) {
        this.session = session;
    }

    public MessageExecutor getMethodExecutor() {
        return methodExecutor;
    }

    public void setMethodExecutor(MessageExecutor methodExecutor) {
        this.methodExecutor = methodExecutor;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes == null ? null : attributes.get(key);
    }

    public <T> T getAttribute(String key, Class<T> type) {
        Object val = getAttribute(key);
        return val == null ? null : type.cast(val);
    }
}
