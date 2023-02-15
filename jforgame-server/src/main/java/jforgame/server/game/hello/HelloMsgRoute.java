package jforgame.server.game.hello;

import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestMapping;

@MessageRoute
public class HelloMsgRoute {

    @RequestMapping
    public Object sayHello(ReqHello request) {
        ResHello response = new ResHello();
        response.setContent("hello, rpc");
        return response;
    }
}
