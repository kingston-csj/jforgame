package jforgame.socket.mina.support.client;

import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.client.Traceable;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.SocketIoDispatcher;
import org.apache.mina.core.session.IoSession;

public class DefaultClientSocketIoHandler extends DefaultSocketIoHandler {
    public DefaultClientSocketIoHandler(SocketIoDispatcher messageDispatcher) {
        super(messageDispatcher);
    }

    @Override
    public void messageReceived(IoSession session, Object data) throws Exception {
        // 客户端增加callback处理
        if (data instanceof Traceable) {
            Traceable traceable = (Traceable) data;
            RpcResponseData responseData = new RpcResponseData();
            responseData.setResponse(data);
            CallBackService.getInstance().fillCallBack(traceable.getIndex(), responseData);
        } else {
            super.messageReceived(session, data);
        }
    }
}
