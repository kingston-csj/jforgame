package jforgame.server.cross.core.callback;

import jforgame.server.cross.core.server.SCSession;
import jforgame.socket.message.Message;

import java.util.HashMap;
import java.util.Map;

public abstract class CallbackHandler {

    private static Map<Integer, CallbackHandler> handlers = new HashMap<>();

    public static void register(CallbackHandler handler) {
        handlers.put(handler.cmdType(), handler);
    }

    public abstract void onRequest(SCSession session, G2FCallBack req);

    public void sendBack(SCSession session, G2FCallBack req, Message response) {
        F2GCallBack callBack = F2GCallBack.valueOf(response);
        callBack.setIndex(req.getIndex());
        callBack.setRpc(req.getRpc());
        session.sendMessage(callBack);
    }

    public abstract int cmdType();

    public static CallbackHandler queryHandler(int type) {
        return handlers.get(type);
    }

}
