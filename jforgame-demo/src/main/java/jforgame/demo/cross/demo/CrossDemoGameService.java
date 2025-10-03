package jforgame.demo.cross.demo;

import jforgame.commons.util.NumberUtil;
import jforgame.demo.ServerConfig;
import jforgame.demo.cross.core.CallBackCommands;
import jforgame.demo.cross.core.F2GCallBack;
import jforgame.demo.cross.core.G2FCallBack;
import jforgame.demo.cross.core.C2SSessionPoolFactory;
import jforgame.demo.cross.core.NSessionPlus;
import jforgame.socket.client.RequestCallback;
import jforgame.socket.client.RpcMessageClient;

public class CrossDemoGameService {

    public static void sayHello() {
        try {
            G2FCallBack req = new G2FCallBack();
            req.addParam("name", "Lily");
            req.setCommand(CallBackCommands.HELLO);

            String matchUrl = ServerConfig.getInstance().getMatchUrl();
            String ip = matchUrl.split(":")[0];
            int port = NumberUtil.intValue(matchUrl.split(":")[1]);
            req.serialize();
            NSessionPlus session = C2SSessionPoolFactory.getInstance().borrowSession(ip, port);

            F2GCallBack response = (F2GCallBack) RpcMessageClient.request(session, req);
            System.err.println("rpc 消息同步调用");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sayHello2() {
        try {
            G2FCallBack req = new G2FCallBack();
            req.addParam("name", "Lily");
            req.setCommand(CallBackCommands.HELLO);
            String matchUrl = ServerConfig.getInstance().getMatchUrl();
            String ip = matchUrl.split(":")[0];
            int port = NumberUtil.intValue(matchUrl.split(":")[1]);
            req.serialize();

            NSessionPlus session = C2SSessionPoolFactory.getInstance().borrowSession(ip, port);

            RpcMessageClient.callBack(session, req, new RequestCallback() {
                @Override
                public void onSuccess(Object callBack) {
                    System.err.println("rpc 消息异步调用");
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
