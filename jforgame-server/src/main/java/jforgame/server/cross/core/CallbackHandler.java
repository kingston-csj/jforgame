package jforgame.server.cross.core;

import jforgame.server.cross.demo.HelloCallbackHandler;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.Message;

import java.util.HashMap;
import java.util.Map;

public abstract class CallbackHandler {

    private static Map<Integer, CallbackHandler> handlers = new HashMap<>();

    public static void register(CallbackHandler handler) {
        handlers.put(handler.cmdType(), handler);
    }

    static {
        register(new HelloCallbackHandler());
    }
    public abstract void onRequest(IdSession session, G2FCallBack req);

    public void sendBack(IdSession session, G2FCallBack req, Message response) {
        F2GCallBack callBack = F2GCallBack.valueOf(response);
        callBack.setIndex(req.getIndex());
        session.send(callBack);
    }

    public abstract int cmdType();

    public static CallbackHandler queryHandler(int type) {
        return handlers.get(type);
    }

}
