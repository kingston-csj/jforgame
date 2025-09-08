package jforgame.socket.netty.support.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.share.message.RequestDataFrame;

/**
 * 回调处理器，用于绑定请求与响应的关系
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