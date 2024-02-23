package jforgame.demo.cross.core;

import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;
import jforgame.socket.share.message.Message;

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