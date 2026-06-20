package jforgame.socket.netty.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.core.client.CallBackService;
import jforgame.socket.core.client.RpcResponseData;
import jforgame.socket.core.protocol.message.RequestDataFrame;

/**
 * Callback handler, used to bind the relationship between request and response.
 */
public class CallbackHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object frame) throws Exception {
        RequestDataFrame dataFrame = (RequestDataFrame) frame;
        Object message = dataFrame.getMessage();
        int msgIndex = dataFrame.getHeader().getIndex();
        if (msgIndex > 0) {
            RpcResponseData responseData = new RpcResponseData();
            responseData.setResponse(message);
            CallBackService.getInstance().fillCallBack(msgIndex, responseData);
        } else {
            // pass the message to the next handler
            context.fireChannelRead(dataFrame);
        }
    }

}