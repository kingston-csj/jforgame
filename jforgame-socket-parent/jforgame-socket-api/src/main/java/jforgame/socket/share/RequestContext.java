package jforgame.socket.share;

import jforgame.socket.share.message.MessageExecutor;
import jforgame.socket.share.message.MessageHeader;

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
}
