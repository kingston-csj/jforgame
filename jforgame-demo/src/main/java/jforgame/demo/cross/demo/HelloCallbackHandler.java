package jforgame.demo.cross.demo;

import jforgame.demo.cross.core.G2FCallBack;
import jforgame.demo.cross.core.CallBackCommands;
import jforgame.demo.cross.core.CallbackHandler;
import jforgame.socket.share.IdSession;

public class HelloCallbackHandler extends CallbackHandler {

    @Override
    public void onRequest(IdSession session, G2FCallBack req) {
        F2GHeartBeat response = new F2GHeartBeat();
        sendBack(session, req, response);
    }

    @Override
    public int cmdType() {
        return CallBackCommands.HELLO;
    }
}
