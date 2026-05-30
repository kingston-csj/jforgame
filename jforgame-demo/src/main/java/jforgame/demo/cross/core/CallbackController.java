package jforgame.demo.cross.core;

import jforgame.socket.session.IdSession;
import jforgame.socket.protocol.annotation.MessageRoute;
import jforgame.socket.protocol.annotation.RequestHandler;
import jforgame.socket.protocol.message.Message;

@MessageRoute
public class CallbackController {

    @RequestHandler
    public void onReqCallBack(IdSession session, G2FCallBack req) {
        int cmdType = req.getCommand();
        CallbackHandler handler = CallbackHandler.queryHandler(cmdType);
        if (handler != null) {
            req.deserialize();
            handler.onRequest(session, req);
        }
    }

    @RequestHandler
    public void onRespCallBack(IdSession session, F2GCallBack response) {
        try {
            Message callback = response.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}