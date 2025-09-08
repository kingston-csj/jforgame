package jforgame.socket.mina.support.client;

import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.mina.support.DefaultSocketIoHandler;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.message.RequestDataFrame;
import org.apache.mina.core.session.IoSession;
/**
 * 默认的客户端SocketIoHandler
 */
public class DefaultClientSocketIoHandler extends DefaultSocketIoHandler {
    public DefaultClientSocketIoHandler(SocketIoDispatcher messageDispatcher) {
        super(messageDispatcher);
    }

    @Override
    public void messageReceived(IoSession session, Object frame) throws Exception {
        RequestDataFrame dataFrame = (RequestDataFrame) frame;
        Object message = dataFrame.getMessage();
        int msgIndex = dataFrame.getHeader().getIndex();
        if (msgIndex > 0) {
            RpcResponseData responseData = new RpcResponseData();
            responseData.setResponse(message);
            CallBackService.getInstance().fillCallBack(msgIndex, responseData);
        } else {
            // pass the message to the next handler
            super.messageReceived(session, dataFrame);
        }
    }
}
