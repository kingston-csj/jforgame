package jforgame.server.cross.demo;

import jforgame.server.cross.core.CrossTransportManager;
import jforgame.server.cross.core.callback.G2FCallBack;
import jforgame.server.cross.core.callback.CallBackCommands;
import jforgame.server.cross.core.callback.RequestCallback;
import jforgame.server.cross.core.client.C2SSessionPoolFactory;
import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.message.Message;

public class CrossDemoGameService {

    public static void sayHello() {
        try {
            G2FCallBack req = new G2FCallBack();
            req.addParam("name", "Lily");
            req.setCommand(CallBackCommands.HELLO);
            CCSession session = C2SSessionPoolFactory.getInstance().borrowCrossSession();
            Message callBack = CrossTransportManager.getInstance().request(session, req);
            System.out.println(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sayHello2() {
        try {
            G2FCallBack req = new G2FCallBack();
            req.addParam("name", "Lily");
            req.setCommand(CallBackCommands.HELLO);
            CCSession session = C2SSessionPoolFactory.getInstance().borrowCrossSession();
            CrossTransportManager.getInstance().request(session, req, new RequestCallback() {
                @Override
                public void onSuccess(Message callBack) {
                    System.out.println(callBack);
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
