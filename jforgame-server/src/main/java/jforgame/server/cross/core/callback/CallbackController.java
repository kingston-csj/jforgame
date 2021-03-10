package jforgame.server.cross.core.callback;

import jforgame.server.cross.core.client.CCSession;
import jforgame.server.cross.core.server.CrossController;
import jforgame.server.cross.core.server.SCSession;
import jforgame.socket.annotation.RequestMapping;
import jforgame.socket.message.Message;

@CrossController
public class CallbackController {

    @RequestMapping
    public void onReqCallBack(SCSession session, G2FCallBack req) {
        int cmdType = req.getCommand();
        CallbackHandler handler = CallbackHandler.queryHandler(cmdType);
        if (handler != null) {
            req.deserialize();
            handler.onRequest(session, req);
        }
    }

    @RequestMapping
    public void onRespCallBack(CCSession session, F2GCallBack response) {
        try {
            Message callback = response.getMessage();
            CallBackService.getInstance().fillCallBack(response.getIndex(), response.getRpc(), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}