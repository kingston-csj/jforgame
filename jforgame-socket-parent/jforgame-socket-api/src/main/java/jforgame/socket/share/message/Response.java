package jforgame.socket.share.message;

/**
 * 通用响应类
 * 对应每一个Req请求协议，都有对应的Res响应协议
 * 推荐使用该类作为响应协议的基类，客户端可以在更高的抽象层统一处理错误码
 * 而对于服务器主动推送给客户端的消息，则无须使用该类
 * 注意：使用该基类，必须确保通信协议层能对父类字段进行序列化和反序列化。
 * json/struct均支持对父类字段进行序列化和反序列化，而原生protobuf尚不支持
 */
public abstract class Response implements Message {

    /**
     * 错误状态码，0为成功，非0为错误码
     */
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
