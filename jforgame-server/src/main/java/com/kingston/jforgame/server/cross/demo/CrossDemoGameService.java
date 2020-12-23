package com.kingston.jforgame.server.cross.demo;

import com.kingston.jforgame.server.cross.core.CrossTransportManager;
import com.kingston.jforgame.server.cross.core.callback.CReqCallBack;
import com.kingston.jforgame.server.cross.core.callback.CallBackCommands;
import com.kingston.jforgame.server.cross.core.client.C2SSessionPoolFactory;
import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.socket.message.Message;

public class CrossDemoGameService {

    public static void sayHello() {
        CReqCallBack req = new CReqCallBack();
        req.addParam("name", "Lily");
        req.setType(CallBackCommands.HELLO);
        CCSession session = C2SSessionPoolFactory.getInstance().borrowCrossSession();
        try {
            Message callBack = CrossTransportManager.getInstance().sendAndReturn(session, req);
            System.out.println(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
