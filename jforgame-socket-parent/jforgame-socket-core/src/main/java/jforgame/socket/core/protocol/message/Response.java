package jforgame.socket.core.protocol.message;

/**
 * Common response class.
 * For each Req request protocol, there is a corresponding Res response protocol.
 * It is recommended to use this class as the base class for response protocols.
 * Clients can uniformly handle error codes at a higher abstraction layer.
 * For messages actively pushed by server to clients, there is no need to use this class.
 * Note: When using this base class, ensure the communication protocol layer can serialize and deserialize parent class fields.
 * json/struct both support serialization/deserialization of parent class fields, while native protobuf does not support it yet.
 */
public abstract class Response implements Message {

    /**
     * Error status code, 0 means success, non-zero means error code
     */
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
