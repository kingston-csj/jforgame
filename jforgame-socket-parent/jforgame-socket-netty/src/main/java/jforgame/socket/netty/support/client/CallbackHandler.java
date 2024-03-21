package jforgame.socket.netty.support.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jforgame.socket.client.CallBackService;
import jforgame.socket.client.RpcResponseData;
import jforgame.socket.client.Traceable;

public class CallbackHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        if (packet instanceof Traceable) {
            Traceable traceable = (Traceable) packet;
            RpcResponseData responseData = new RpcResponseData();
            responseData.setResponse(packet);
            CallBackService.getInstance().fillCallBack(traceable.getIndex(), responseData);
        } else {
            // pass the message to the next handler
            context.fireChannelRead(packet);
        }
    }

}