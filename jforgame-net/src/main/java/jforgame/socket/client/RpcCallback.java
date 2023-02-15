package jforgame.socket.client;

import jforgame.socket.IdSession;

public interface RpcCallback {

    Object request(IdSession session, Traceful request) throws CallbackTimeoutException;

}
