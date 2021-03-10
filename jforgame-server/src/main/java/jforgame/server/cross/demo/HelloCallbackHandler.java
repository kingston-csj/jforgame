package jforgame.server.cross.demo;

import jforgame.server.cross.core.callback.G2FCallBack;
import jforgame.server.cross.core.callback.CallBackCommands;
import jforgame.server.cross.core.callback.CallbackHandler;
import jforgame.server.cross.core.server.SCSession;

public class HelloCallbackHandler extends CallbackHandler {

    @Override
    public void onRequest(SCSession session, G2FCallBack req) {
        F2GHeartBeat response = new F2GHeartBeat();
        sendBack(session, req, response);
    }

    @Override
    public int cmdType() {
        return CallBackCommands.HELLO;
    }
}
