package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.server.cross.core.server.CrossController;
import com.kingston.jforgame.server.cross.core.server.SCSession;
import com.kingston.jforgame.server.utils.JsonUtils;
import com.kingston.jforgame.socket.annotation.RequestMapping;
import com.kingston.jforgame.socket.message.Message;

@CrossController
public class CallbackController {

    @RequestMapping
    public void onReqCallBack(SCSession session, CReqCallBack req) {
        int cmdType = req.getCmd();
        CallbackHandler handler = CallbackHandler.queryHandler(cmdType);
        if (handler != null) {
            req.deserialize();
            handler.onRequest(session, req);
        }
    }

    @RequestMapping
    public void onRespCallBack(CCSession session, CRespCallBack respCallBack) {
        String json = respCallBack.getData();
        try {
            Message message = (Message) JsonUtils.string2Object(json, Class.forName(respCallBack.getMsgClass()));
            CallBackService.getInstance().fillCallBack(respCallBack.getIndex(), message);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}