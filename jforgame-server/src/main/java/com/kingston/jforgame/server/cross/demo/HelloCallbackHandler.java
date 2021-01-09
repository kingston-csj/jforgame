package com.kingston.jforgame.server.cross.demo;

import com.kingston.jforgame.server.cross.core.callback.CReqCallBack;
import com.kingston.jforgame.server.cross.core.callback.CallBackCommands;
import com.kingston.jforgame.server.cross.core.callback.CallbackHandler;
import com.kingston.jforgame.server.cross.core.server.SCSession;

public class HelloCallbackHandler extends CallbackHandler {

    @Override
    public void onRequest(SCSession session, CReqCallBack req) {
        CRespCrossHeartBeat response = new CRespCrossHeartBeat();
        sendBack(session, req, response);
    }

    @Override
    public int cmdType() {
        return CallBackCommands.HELLO;
    }
}
