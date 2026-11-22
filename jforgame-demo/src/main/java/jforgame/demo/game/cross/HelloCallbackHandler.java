package jforgame.demo.game.cross;

import jforgame.demo.framework.cross.core.G2FCallBack;
import jforgame.demo.framework.cross.core.CallBackCommands;
import jforgame.demo.framework.cross.core.CallbackHandler;
import jforgame.socket.core.session.IdSession;

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
