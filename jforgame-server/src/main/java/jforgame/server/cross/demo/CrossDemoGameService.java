package jforgame.server.cross.demo;

import jforgame.common.utils.NumberUtil;
import jforgame.server.ServerConfig;
import jforgame.server.cross.core.callback.CallBackCommands;
import jforgame.server.cross.core.callback.G2FCallBack;
import jforgame.server.cross.core.callback.RequestCallback;
import jforgame.server.cross.core.client.CrossTransportManager;
import jforgame.socket.HostAndPort;
import jforgame.socket.message.Message;

public class CrossDemoGameService {

    public static void sayHello() {
        try {
            G2FCallBack req = new G2FCallBack();
            req.addParam("name", "Lily");
            req.setCommand(CallBackCommands.HELLO);

            String matchUrl = ServerConfig.getInstance().getMatchUrl();
            String ip = matchUrl.split(":")[0];
            int port = NumberUtil.intValue(matchUrl.split(":")[1]);
            Message callBack = CrossTransportManager.getInstance().request(HostAndPort.valueOf(ip, port), req);
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
            String matchUrl = ServerConfig.getInstance().getMatchUrl();
            String ip = matchUrl.split(":")[0];
            int port = NumberUtil.intValue(matchUrl.split(":")[1]);
            CrossTransportManager.getInstance().request(HostAndPort.valueOf(ip, port), req, new RequestCallback() {
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
