package jforgame.socket.share.message;

/**
 * @Author: caochaojie
 * @Date: 2025-02-17 00:36
 */
public abstract class SCMessage {
    protected int errorCode;

    protected int cmd;

    public SCMessage() {

    }

    public int getCmd() {
        return cmd;
    }

    public SCMessage(int code) {
        this.errorCode = code;
    }

    public SCMessage(Message message) {
        this.errorCode = 2;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
