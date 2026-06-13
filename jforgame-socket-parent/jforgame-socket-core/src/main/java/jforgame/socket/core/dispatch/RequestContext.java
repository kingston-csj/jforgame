package jforgame.socket.core.dispatch;


import jforgame.socket.core.protocol.message.MessageHeader;
import jforgame.socket.core.registry.MessageExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 对于客户端的每一个请求，在生命周期内，都有一个供框架执行的上下文
 */
public class RequestContext {

    /**
     * 消息执行器
     */
    private MessageExecutor methodExecutor;

    /**
     * 请求消息包
     */
    private Object request;

    /**
     * 请求消息包对应的包头
     */
    private MessageHeader header;

    /**
     * 实际传递到消息执行者的具体参数
     */
    private Object[] params;

    /**
     * 处理过程中遇到的异常
     */
    private Throwable error;

    /**
     * 对应的处理结果(可为空)
     */
    private Object response;

    /**
     * 拓展属性
     */
    private Map<String, Object> attributes;

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
