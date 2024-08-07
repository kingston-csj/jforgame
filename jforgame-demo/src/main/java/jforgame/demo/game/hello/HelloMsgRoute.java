package jforgame.demo.game.hello;

import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class HelloMsgRoute {

    @RequestHandler
    public Object sayHello(IdSession session, int index, ReqHello request) {
        ResHello response = new ResHello();
        response.setContent("hello, rpc");
        return response;
    }
}
