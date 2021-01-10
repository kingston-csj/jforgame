package com.kingston.jforgame.server.cross.demo;

import com.kingston.jforgame.server.cross.core.CrossTransportManager;
import com.kingston.jforgame.server.cross.core.callback.CReqCallBack;
import com.kingston.jforgame.server.cross.core.callback.CallBackCommands;
import com.kingston.jforgame.server.cross.core.callback.CallbackAction;
import com.kingston.jforgame.server.cross.core.client.C2SSessionPoolFactory;
import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.socket.message.Message;

public class CrossDemoGameService {

    public static void sayHello() {
        CReqCallBack req = new CReqCallBack();
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
        CReqCallBack req = new CReqCallBack();
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
