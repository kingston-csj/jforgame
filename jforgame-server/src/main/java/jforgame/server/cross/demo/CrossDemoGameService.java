package jforgame.server.cross.demo;

import jforgame.server.cross.core.CrossTransportManager;
import jforgame.server.cross.core.callback.G2FCallBack;
import jforgame.server.cross.core.callback.CallBackCommands;
import jforgame.server.cross.core.callback.CallbackAction;
import jforgame.server.cross.core.client.C2SSessionPoolFactory;
import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.message.Message;

public class CrossDemoGameService {

    public static void sayHello() {
        G2FCallBack req = new G2FCallBack();
        req.addParam("name", "Lily");
        req.setCommand(CallBackCommands.HELLO);
        CCSession session = C2SSessionPoolFactory.getInstance().borrowCrossSession();
        try {
            Message callBack = CrossTransportManager.getInstance().sendWithReturn(session, req);
            System.out.println(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sayHello2() {
        G2FCallBack req = new G2FCallBack();
        req.addParam("name", "Lily");
        req.setCommand(CallBackCommands.HELLO);
        CCSession session = C2SSessionPoolFactory.getInstance().borrowCrossSession();
        try {
            CrossTransportManager.getInstance().callback(session, req, new CallbackAction() {
                @Override
                public void onMessageReceive(Message callBack) {
                    System.out.println(callBack);
                }

                @Override
                public void onError() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
