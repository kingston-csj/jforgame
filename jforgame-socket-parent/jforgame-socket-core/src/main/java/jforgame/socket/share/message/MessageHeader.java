package jforgame.socket.share.message;

/**
 * 私有协议栈——消息包头定义
 * 这里用接口定义，用户可根据需要，对指定字段（例如cmd）的类型进行修改
 * 例如：网络io可将cmd定义为short型，应用程序统一以int接收。
 * 这样可节省部分网络IO，同时，这类对象属于“短命小对象”，对gc影响很小
 */
public interface MessageHeader {


    byte[] write();

    void read(byte[] bytes);

    /**
     * 私有协议栈的总长度，包括包头+包体
     * @return 私有协议栈的总长度
     */
    int getMsgLength();

    void setMsgLength(int msgLength);

    /**
     * 消息包序号（由客户端保证自增长）
     * 可用于客户端回调，消息重放检测
     * @return 消息包序号
     */
    int getIndex();


    void setIndex(int index);


    /**
     * 消息类型
     * @return 消息类型
     */
    int getCmd();


    void setCmd(int cmd);

}
